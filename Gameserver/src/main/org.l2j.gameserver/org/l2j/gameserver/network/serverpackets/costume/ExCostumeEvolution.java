package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExCostumeEvolution extends ServerPacket {

    private boolean success;
    private Collection<CostumeData> targetCostumes;
    private CostumeData resultCostume;

    private ExCostumeEvolution() {
    }

    public static ExCostumeEvolution failed() {
        return new ExCostumeEvolution();
    }

    public static ExCostumeEvolution success(Set<CostumeData> costume, CostumeData resultCostume) {
        var packet = new ExCostumeEvolution();
        packet.success = true;
        packet.targetCostumes = costume;
        packet.resultCostume = resultCostume;
        return packet;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_COSTUME_EVOLUTION);
        writeByte(success);
        writeInt(targetCostumes.size());

        for (CostumeData targetCostume : targetCostumes) {
            writeInt(targetCostume.getId());
            writeLong(targetCostume.getAmount());
        }
        if(nonNull(resultCostume)) {
            writeInt(1);
            writeInt(resultCostume.getId());
            writeLong(resultCostume.getAmount());
        } else {
            writeInt(0);
        }
    }
}
