package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemauction.ItemAuction;
import org.l2j.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2j.gameserver.model.itemcontainer.Inventory;

/**
 * @author Forsaiken
 */
public final class RequestBidItemAuction extends ClientPacket {
    private int _instanceId;
    private long _bid;

    @Override
    public void readImpl() {
        _instanceId = readInt();
        _bid = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        // can't use auction fp here
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("auction")) {
            activeChar.sendMessage("You are bidding too fast.");
            return;
        }

        if ((_bid < 0) || (_bid > Inventory.MAX_ADENA)) {
            return;
        }

        final ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(_instanceId);
        if (instance != null) {
            final ItemAuction auction = instance.getCurrentAuction();
            if (auction != null) {
                auction.registerBid(activeChar, _bid);
            }
        }
    }
}