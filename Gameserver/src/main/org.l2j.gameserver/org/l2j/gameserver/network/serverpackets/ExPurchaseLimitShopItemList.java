package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

public class ExPurchaseLimitShopItemList extends ServerPacket {
    private void writeIngredientsBlock(List<ItemHolder> ingredients) {
        //CostItemId
        for (var i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                writeInt(ingredients.get(i).getId());
            }
            else {
                writeInt(0);
            }
        }

        //CostItemAmount
        for (var i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                writeLong(ingredients.get(i).getCount());
            }
            else {
                writeLong(0);
            }
        }

        //CostItemSaleAmount
        for (var i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                writeLong(ingredients.get(i).getCount());
            }
            else {
                writeLong(0);
            }
        }

        writeInt(0); //CostItemSaleRate_1
        writeInt(0); //CostItemSaleRate_2
        writeInt(0); //CostItemSaleRate_3
    }

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST);

        var productInfos = LCoinShopData.getInstance().getProductInfos();

        writeByte(0x03);
        writeInt(productInfos.size());

        productInfos.forEach( (var key, var product) -> {
            writeInt(key);
            writeInt(product.getProduction().getId());
            writeByte(product.getLimitPerDay() > 0 ? 1 : 0);
            writeShort(product.getMinLevel());
            writeInt(product.getLimitPerDay());
            writeIngredientsBlock(product.getIngredients());
            writeInt(product.getIngredients().size());
            writeByte(product.getCategory().ordinal() + 1);
            writeByte(product.isEvent());
            writeInt(0); //EventRemainSec
            writeInt(0); //SaleRemainSec
        });

        writeShort(0);
    }
}
