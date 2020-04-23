package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public class PledgeShowMemberListAll extends PledgeAbstractPacket {
    private final SubPledgeData pledge;
    private final boolean isSubPledge;

    private PledgeShowMemberListAll(Clan clan, SubPledgeData pledge, boolean isSubPledge) {
        super(clan);
        this.pledge = pledge;
        this.isSubPledge = isSubPledge;
    }

    public static void sendAllTo(Player player) {
        final Clan clan = player.getClan();
        if (clan != null) {
            for (var subPledge : clan.getAllSubPledges()) {
                player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
            }
            player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ALL);

        writeInt(!isSubPledge);
        writeInt(clan.getId());
        writeInt(getSettings(ServerSettings.class).serverId());

        var pledgeId = isNull(pledge) ? 0x00 : pledge.getId();
        var leaderName = isNull(pledge) ? clan.getLeaderName() : PlayerNameTable.getInstance().getNameById(pledge.getLeaderId());

        writeInt(pledgeId);
        writeString(isNull(pledge) ? clan.getName() : pledge.getName());
        writeString(leaderName);

        writeClanInfo(pledgeId);

        clan.forEachMember(this::writeMemberInfo, m -> m.getPledgeType() == pledgeId);
    }

    protected void writeMemberInfo(ClanMember m) {
        writeString(m.getName());
        writeInt(m.getLevel());
        writeInt(m.getClassId());

        var player = m.getPlayerInstance();
        if (nonNull(player)) {
            writeInt(player.getAppearance().isFemale()); // no visible effect
            writeInt(player.getRace().ordinal()); // packet.putInt(1);
        } else {
            writeInt(0x01); // no visible effect
            writeInt(0x01); // packet.putInt(1);
        }
        writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
        writeInt(m.getSponsor() != 0);
        writeByte(m.getOnlineStatus());
    }

}
