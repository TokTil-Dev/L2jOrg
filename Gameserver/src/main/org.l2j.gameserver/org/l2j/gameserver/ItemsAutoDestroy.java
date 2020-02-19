package org.l2j.gameserver;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.settings.GeneralSettings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;

public final class ItemsAutoDestroy {
    private final List<Item> _items = new LinkedList<>();

    private ItemsAutoDestroy() {
        ThreadPool.scheduleAtFixedRate(this::removeItems, 5000, 5000);
    }

    public synchronized void addItem(Item item) {
        item.setDropTime(System.currentTimeMillis());
        _items.add(item);
    }

    private synchronized void removeItems() {
        if (_items.isEmpty()) {
            return;
        }

        final long curtime = System.currentTimeMillis();
        final Iterator<Item> itemIterator = _items.iterator();
        while (itemIterator.hasNext()) {
            final Item item = itemIterator.next();
            if ((item.getDropTime() == 0) || (item.getItemLocation() != ItemLocation.VOID)) {
                itemIterator.remove();
            } else {
                final long autoDestroyTime;
                if (item.getTemplate().getAutoDestroyTime() > 0) {
                    autoDestroyTime = item.getTemplate().getAutoDestroyTime();
                } else if (item.getTemplate().hasExImmediateEffect()) {
                    autoDestroyTime = Config.HERB_AUTO_DESTROY_TIME;
                } else {
                    autoDestroyTime = ((Config.AUTODESTROY_ITEM_AFTER == 0) ? 3600000 : Config.AUTODESTROY_ITEM_AFTER * 1000);
                }

                if ((curtime - item.getDropTime()) > autoDestroyTime) {
                    item.decayMe();
                    itemIterator.remove();
                    if (getSettings(GeneralSettings.class).saveDroppedItems()) {
                        ItemsOnGroundManager.getInstance().removeObject(item);
                    }
                }
            }
        }
    }

    public static ItemsAutoDestroy getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemsAutoDestroy INSTANCE = new ItemsAutoDestroy();
    }
}