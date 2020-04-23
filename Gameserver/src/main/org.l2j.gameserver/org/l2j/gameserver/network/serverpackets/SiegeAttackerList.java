package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = unknow (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @author KenM
 */
public final class SiegeAttackerList extends ServerPacket {
    private final Castle _castle;

    public SiegeAttackerList(Castle castle) {
        _castle = castle;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CASTLE_SIEGE_ATTACKER_LIST);

        writeInt(_castle.getId());
        writeInt(0x00); // 0
        writeInt(0x01); // 1
        writeInt(0x00); // 0
        final int size = _castle.getSiege().getAttackerClans().size();
        if (size > 0) {
            Clan clan;

            writeInt(size);
            writeInt(size);
            for (var siegeclan : _castle.getSiege().getAttackerClans().values()) {
                clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
                if (clan == null) {
                    continue;
                }

                writeInt(clan.getId());
                writeString(clan.getName());
                writeString(clan.getLeaderName());
                writeInt(clan.getCrestId());
                writeInt(0x00); // signed time (seconds) (not storated by L2J)
                writeInt(clan.getAllyId());
                writeString(clan.getAllyName());
                writeString(""); // AllyLeaderName
                writeInt(clan.getAllyCrestId());
            }
        } else {
            writeInt(0x00);
            writeInt(0x00);
        }
    }

}
