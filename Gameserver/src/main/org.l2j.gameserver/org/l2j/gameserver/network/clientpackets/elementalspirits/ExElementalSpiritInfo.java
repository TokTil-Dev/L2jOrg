package org.l2j.gameserver.network.clientpackets.elementalspirits;


import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

public class ExElementalSpiritInfo extends IClientIncomingPacket {

    private int _id;

    public void readImpl() {
        this._id = readByte();
    }

    public void runImpl() {
        client.sendPacket(new ElementalSpiritInfo());
    }

}