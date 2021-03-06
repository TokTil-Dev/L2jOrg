package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class MoveToLocation extends ServerPacket {
    private final int _charObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _xDst;
    private final int _yDst;
    private final int _zDst;

    public MoveToLocation(Creature cha) {
        _charObjId = cha.getObjectId();
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _xDst = cha.getXdestination();
        _yDst = cha.getYdestination();
        _zDst = cha.getZdestination();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MOVE_TO_LOCATION);

        writeInt(_charObjId);

        writeInt(_xDst);
        writeInt(_yDst);
        writeInt(_zDst);

        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
