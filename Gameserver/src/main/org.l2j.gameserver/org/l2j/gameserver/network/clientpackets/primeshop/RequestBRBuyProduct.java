package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRBuyProduct extends RequestBuyProduct {

    private int productId;
    private int count;

    @Override
    public void readImpl() {
        productId = readInt();
        count = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class)) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SERVER_ERROR));
            return;
        }
        try {
            activeChar.addRequest(new PrimeShopRequest(activeChar));
            final PrimeShopProduct item = PrimeShopData.getInstance().getItem(productId);

            if (validatePlayer(item, count, activeChar) && processPayment(activeChar, item, count)) {

                for (PrimeShopItem subItem : item.getItems()) {
                    activeChar.addItem("PrimeShop", subItem.getId(), subItem.getCount() * count, activeChar, true);
                }

                client.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                client.sendPacket(new ExBRGamePoint());
                getDAO(PrimeShopDAO.class).addHistory(productId, count, activeChar.getObjectId());
                if(item.isVipGift()) {
                    client.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
                }

            }
        } finally {
            activeChar.removeRequest(PrimeShopRequest.class);
        }
    }
}
