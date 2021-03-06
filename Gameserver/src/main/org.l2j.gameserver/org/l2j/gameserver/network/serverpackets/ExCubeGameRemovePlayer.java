package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author mrTJO
 */
public class ExCubeGameRemovePlayer extends ServerPacket {
    Player _player;
    boolean _isRedTeam;

    /**
     * Remove Player from Minigame Waiting List
     *
     * @param player    Player to Remove
     * @param isRedTeam Is Player from Red Team?
     */
    public ExCubeGameRemovePlayer(Player player, boolean isRedTeam) {
        _player = player;
        _isRedTeam = isRedTeam;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_BLOCK_UP_SET_LIST);

        writeInt(0x02);

        writeInt(0xffffffff);

        writeInt(_isRedTeam ? 0x01 : 0x00);
        writeInt(_player.getObjectId());
    }

}
