package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;

/**
 * @author Sdw
 */
public class RequestNewEnchantRetryToPutItems extends ClientPacket {
    private int _firstItemObjectId;
    private int _secondItemObjectId;

    @Override
    public void readImpl() {
        _firstItemObjectId = readInt();
        _secondItemObjectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = new CompoundRequest(activeChar);
        if (!activeChar.addRequest(request)) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns first item.
        request.setItemOne(_firstItemObjectId);
        final Item itemOne = request.getItemOne();
        if (itemOne == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        // Make sure player owns second item.
        request.setItemTwo(_secondItemObjectId);
        final Item itemTwo = request.getItemTwo();
        if (itemTwo == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        client.sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
    }
}
