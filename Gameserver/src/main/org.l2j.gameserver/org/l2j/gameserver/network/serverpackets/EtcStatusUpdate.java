package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Luca Baldi
 */
public class EtcStatusUpdate extends ServerPacket {
    private final Player player;
    private int toggles;

    public EtcStatusUpdate(Player activeChar) {
        player = activeChar;
        toggles = player.isMessageRefusing() || player.isChatBanned() || player.isSilenceMode() ? 1 : 0;
        toggles |= player.isInsideZone(ZoneType.DANGER_AREA) ? 2 : 0;
        toggles |= player.hasCharmOfCourage() ? 4 : 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ETC_STATUS_UPDATE);

        writeByte(player.getCharges()); // 1-7 increase force, lvl
        writeInt(player.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
        writeByte(0); // Weapon Grade Penalty [1-4]
        writeByte(0); // Armor Grade Penalty [1-4]
        writeByte(0); // Death Penalty [1-15, 0 = disabled)], not used anymore in Ertheia
        writeByte(player.getChargedSouls());
        writeByte(toggles);
        writeByte(player.getShadowSouls());
        writeByte(player.getShineSouls());
    }

}
