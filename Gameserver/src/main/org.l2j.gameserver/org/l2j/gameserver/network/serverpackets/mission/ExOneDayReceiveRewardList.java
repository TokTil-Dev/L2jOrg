package org.l2j.gameserver.network.serverpackets.mission;

import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.cron4j.Predictor;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class ExOneDayReceiveRewardList extends ServerPacket {
    private static final Function<String, Long> remainTime = pattern -> (new Predictor(pattern).nextMatchingTime() - System.currentTimeMillis()) / 1000;
    private final Player player;
    private final Collection<MissionDataHolder> missions;
    private final long dayRemainTime;
    private final long weekRemainTime;
    private final long monthRemainTime;

    public ExOneDayReceiveRewardList(Player player, boolean sendRewards) {
        this.player = player;
        this.missions = sendRewards ? MissionData.getInstance().getMissions(player) : Collections.emptyList();
        dayRemainTime = remainTime.apply("30 6 * * *");
        weekRemainTime = remainTime.apply("30 6 * * 1");
        monthRemainTime = remainTime.apply("30 6 1 * *");
    }

    @Override
    public void writeImpl(GameClient client) {
        if (!MissionData.getInstance().isAvailable()) {
            return;
        }

        writeId(ServerPacketId.EX_ONE_DAY_RECEIVE_REWARD_LIST);

        writeInt((int) dayRemainTime);
        writeInt((int) weekRemainTime);
        writeInt((int) monthRemainTime);
        writeByte(0x17);
        writeInt(player.getClassId().getId());
        writeInt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)); // Day of week
        writeInt(missions.size());
        for (MissionDataHolder mission : missions) {
            writeShort(mission.getId());
            writeByte(mission.getStatus(player));
            writeByte((mission.getRequiredCompletions() > 1));
            writeInt(mission.getProgress(player));
            writeInt(mission.getRequiredCompletions());
        }
    }

}
