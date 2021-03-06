package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * update 27.8.10
 *
 * @author kerberos, JIV
 */
public class ExStopMoveInAirShip extends ServerPacket {
    private final Player _activeChar;
    private final int _shipObjId;
    private final int _h;
    private final Location _loc;

    public ExStopMoveInAirShip(Player player, int shipObjId) {
        _activeChar = player;
        _shipObjId = shipObjId;
        _h = player.getHeading();
        _loc = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_STOP_MOVE_IN_AIR_SHIP);

        writeInt(_activeChar.getObjectId());
        writeInt(_shipObjId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(_h);
    }

}
