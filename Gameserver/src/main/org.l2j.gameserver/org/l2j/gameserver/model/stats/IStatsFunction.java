package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.transform.TransformType;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author UnAfraid
 */
@FunctionalInterface
public interface IStatsFunction {
    /**
     * @param item
     * @param blessedBonus
     * @param enchant
     * @return
     */
    static double calcEnchantDefBonus(Item item, double blessedBonus, int enchant) {
        return enchant + (3 * Math.max(0, enchant - 3));
    }

    /**
     * @param item
     * @param blessedBonus
     * @param enchant
     * @return
     */
    static double calcEnchantMatkBonus(Item item, double blessedBonus, int enchant) {
        switch (item.getItem().getCrystalType()) {
            case S: {
                // M. Atk. increases by 4 for all weapons.
                // Starting at +4, M. Atk. bonus double.
                return (4 * enchant) + (8 * Math.max(0, enchant - 3));
            }
            case A:
            case B:
            case C: {
                // M. Atk. increases by 3 for all weapons.
                // Starting at +4, M. Atk. bonus double.
                return (3 * enchant) + (6 * Math.max(0, enchant - 3));
            }
            default: {
                // M. Atk. increases by 2 for all weapons. Starting at +4, M. Atk. bonus double.
                // Starting at +4, M. Atk. bonus double.
                return (2 * enchant) + (4 * Math.max(0, enchant - 3));
            }
        }
    }

    /**
     * @param item
     * @param blessedBonus
     * @param enchant
     * @return
     */
    static double calcEnchantedPAtkBonus(Item item, double blessedBonus, int enchant) {
        switch (item.getItem().getCrystalType()) {
            case S: {
                if (item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND && (item.getWeaponItem().getItemType() != WeaponType.POLE)) {
                    if (item.getWeaponItem().getItemType().isRanged()) {
                        // P. Atk. increases by 10 for bows.
                        // Starting at +4, P. Atk. bonus double.
                        return (10 * enchant) + (20 * Math.max(0, enchant - 3));
                    }
                    // P. Atk. increases by 6 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
                    // Starting at +4, P. Atk. bonus double.
                    return (6 * enchant) + (12 * Math.max(0, enchant - 3));
                }
                // P. Atk. increases by 5 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
                // Starting at +4, P. Atk. bonus double.
                return (5 * enchant) + (10 * Math.max(0, enchant - 3));
            }
            case A: {
                if (item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND && (item.getWeaponItem().getItemType() != WeaponType.POLE)) {
                    if (item.getWeaponItem().getItemType().isRanged()) {
                        // P. Atk. increases by 8 for bows.
                        // Starting at +4, P. Atk. bonus double.
                        return (8 * enchant) + (16 * Math.max(0, enchant - 3));
                    }
                    // P. Atk. increases by 5 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
                    // Starting at +4, P. Atk. bonus double.
                    return (5 * enchant) + (10 * Math.max(0, enchant - 3));
                }
                // P. Atk. increases by 4 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
                // Starting at +4, P. Atk. bonus double.
                return (4 * enchant) + (8 * Math.max(0, enchant - 3));
            }
            case B:
            case C: {
                if (item.getWeaponItem().getBodyPart() == ItemTemplate.SLOT_LR_HAND && (item.getWeaponItem().getItemType() != WeaponType.POLE)) {
                    if (item.getWeaponItem().getItemType().isRanged()) {
                        // P. Atk. increases by 6 for bows.
                        // Starting at +4, P. Atk. bonus double.
                        return (6 * enchant) + (12 * Math.max(0, enchant - 3));
                    }
                    // P. Atk. increases by 4 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
                    // Starting at +4, P. Atk. bonus double.
                    return (4 * enchant) + (8 * Math.max(0, enchant - 3));
                }
                // P. Atk. increases by 3 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
                // Starting at +4, P. Atk. bonus double.
                return (3 * enchant) + (6 * Math.max(0, enchant - 3));
            }
            default: {
                if (item.getWeaponItem().getItemType().isRanged()) {
                    // Bows increase by 4.
                    // Starting at +4, P. Atk. bonus double.
                    return (4 * enchant) + (8 * Math.max(0, enchant - 3));
                }
                // P. Atk. increases by 2 for all weapons with the exception of bows.
                // Starting at +4, P. Atk. bonus double.
                return (2 * enchant) + (4 * Math.max(0, enchant - 3));
            }
        }
    }

    default void throwIfPresent(Optional<Double> base) {
        if (base.isPresent()) {
            throw new IllegalArgumentException("base should not be set for " + getClass().getSimpleName());
        }
    }

    default double calcEnchantBodyPart(Creature creature, int... slots) {
        double value = 0;
        for (int slot : slots) {
            final Item item = creature.getInventory().getPaperdollItemByL2ItemId(slot);
            // TODO Confirm if the bonus is applied for any Grade
            if ((item != null) && (item.getEnchantLevel() >= 4)) {
                value += calcEnchantBodyPartBonus(item.getEnchantLevel(), item.getItem().isBlessed());
            }
        }
        return value;
    }

    default double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed) {
        return 0;
    }

    default double calcWeaponBaseValue(Creature creature, Stats stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
        double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            final Item weapon = pet.getActiveWeaponInstance();
            final double baseVal = stat == Stats.PHYSICAL_ATTACK ? pet.getPetLevelData().getPetPAtk() : stat == Stats.MAGIC_ATTACK ? pet.getPetLevelData().getPetMAtk() : baseTemplateValue;
            baseValue = baseVal + (weapon != null ? weapon.getItem().getStats(stat, baseVal) : 0);
        } else if (isPlayer(creature) && (!creature.isTransformed() || (creature.getTransformation().get().getType() == TransformType.COMBAT) || (creature.getTransformation().get().getType() == TransformType.MODE_CHANGE))) {
            final Item weapon = creature.getActiveWeaponInstance();
            baseValue = (weapon != null ? weapon.getItem().getStats(stat, baseTemplateValue) : baseTemplateValue);
        }

        return baseValue;
    }

    default double calcWeaponPlusBaseValue(Creature creature, Stats stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
        double baseValue = creature.getTransformation().filter(transform -> !transform.isStance()).map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);

        if (isPlayable(creature)) {
            final Inventory inv = creature.getInventory();
            if (inv != null) {
                for (Item item : inv.getPaperdollItems(Item::isEquipped)) {
                    baseValue += item.getItem().getStats(stat, 0);
                }
            }
        }

        return baseValue;
    }

    default double calcEnchantedItemBonus(Creature creature, Stats stat) {
        if (!isPlayer(creature)) {
            return 0;
        }

        double value = 0;
        for (Item equippedItem : creature.getInventory().getPaperdollItems(Item::isEquipped, Item::isEnchanted)) {
            final ItemTemplate item = equippedItem.getItem();
            final long bodypart = item.getBodyPart();
            if ((bodypart == ItemTemplate.SLOT_HAIR) || //
                    (bodypart == ItemTemplate.SLOT_HAIR2) || //
                    (bodypart == ItemTemplate.SLOT_HAIRALL)) {
                // TODO: Item after enchant shows pDef, but scroll says mDef increase.
                if ((stat != Stats.PHYSICAL_DEFENCE) && (stat != Stats.MAGICAL_DEFENCE)) {
                    continue;
                }
            } else if (item.getStats(stat, 0) <= 0) {
                continue;
            }

            final double blessedBonus = item.isBlessed() ? 1.5 : 1;
            int enchant = equippedItem.getEnchantLevel();

            if (creature.getActingPlayer().isInOlympiadMode() && (Config.ALT_OLY_ENCHANT_LIMIT >= 0) && (enchant > Config.ALT_OLY_ENCHANT_LIMIT)) {
                enchant = Config.ALT_OLY_ENCHANT_LIMIT;
            }

            if ((stat == Stats.MAGICAL_DEFENCE) || (stat == Stats.PHYSICAL_DEFENCE)) {
                value += calcEnchantDefBonus(equippedItem, blessedBonus, enchant);
            } else if (stat == Stats.MAGIC_ATTACK) {
                value += calcEnchantMatkBonus(equippedItem, blessedBonus, enchant);
            } else if ((stat == Stats.PHYSICAL_ATTACK) && equippedItem.isWeapon()) {
                value += calcEnchantedPAtkBonus(equippedItem, blessedBonus, enchant);
            }
        }
        return value;
    }

    default double validateValue(Creature creature, double value, double minValue, double maxValue) {
        if ((value > maxValue) && !creature.canOverrideCond(PcCondOverride.MAX_STATS_VALUE)) {
            return maxValue;
        }

        return Math.max(minValue, value);
    }

    double calc(Creature creature, Optional<Double> base, Stats stat);
}
