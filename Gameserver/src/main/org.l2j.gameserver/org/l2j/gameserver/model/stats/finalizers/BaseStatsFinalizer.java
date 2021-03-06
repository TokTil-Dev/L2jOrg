/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class BaseStatsFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);

        // Apply template value
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);

        // Should not apply armor set and henna bonus to summons.
        if (isPlayer(creature))
        {
            final Player player = creature.getActingPlayer();
            final Set<ArmorSet> appliedSets = new HashSet<>(2);

            // Armor sets calculation
            for (Item item : player.getInventory().getPaperdollItems()) {
                for (ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId())) {
                    if ((set.getPiecesCount(player, Item::getId) >= set.getMinimumPieces()) && appliedSets.add(set)) {
                        baseValue += set.getStatsBonus(BaseStats.valueOf(stat));
                    }
                }
            }

            // Henna calculation
            baseValue += player.getHennaValue(BaseStats.valueOf(stat));
        }

        return validateValue(creature, Stats.defaultValue(creature, stat, baseValue), 1, BaseStats.MAX_STAT_VALUE - 1);
    }

}
