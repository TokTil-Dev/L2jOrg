package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect extends ServerPacket {
    private final Npc _npc;

    public NpcInfoAbnormalVisualEffect(Npc npc) {
        _npc = npc;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NPC_INFO_ABNORMAL_VISUAL_EFFECT);

        writeInt(_npc.getObjectId());
        writeInt(_npc.getTransformationDisplayId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size());
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort((short) abnormalVisualEffect.getClientId());
        }
    }

}
