package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ReceiveVipInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var player = client.getPlayer();
        var vipData = VipEngine.getInstance();
        var vipTier = player.getVipTier();

        var vipDuration = (int) ChronoUnit.SECONDS.between(Instant.now(), Instant.ofEpochMilli(client.getVipTierExpiration()));

        writeId(ServerPacketId.RECEIVE_VIP_INFO);
        writeByte(vipTier); // VIP Current level ( MAX 7 )
        writeLong(client.getVipPoints()); // VIP Current Points
        writeInt(vipDuration); // VIP Benefit Duration Seconds
        writeLong(vipData.getPointsToLevel(vipTier + 1)); // VIP Points to next Level
        writeLong(vipData.getPointsDepreciatedOnLevel(vipTier)); // VIP Points used on  30 days period
        writeByte(vipTier); // VIP tier
        writeLong(vipData.getPointsToLevel(vipTier)); // VIP Current Level Requirement Points
    }

}
