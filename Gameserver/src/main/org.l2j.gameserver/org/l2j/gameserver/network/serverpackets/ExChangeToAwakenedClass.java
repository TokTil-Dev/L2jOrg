package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExChangeToAwakenedClass extends ServerPacket {
    private final int _classId;

    public ExChangeToAwakenedClass(int classId) {
        _classId = classId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_TO_AWAKENED_CLASS);

        writeInt(_classId);
    }

}