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
package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.taskmanager.DecayTaskManager;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Resurrection effect implementation.
 * @author Adry_85
 */
public final class Resurrection extends AbstractEffect
{
	private final int _power;
	
	public Resurrection(StatsSet params)
	{
		_power = params.getInt("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESURRECTION;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (isPlayer(effector))
		{
			final Player player = effected.getActingPlayer();
			if (!player.isResurrectionBlocked() && !player.isReviveRequested())
			{
				effected.getActingPlayer().reviveRequest(effector.getActingPlayer(), skill, isPet(effected), _power);
			}
		}
		else
		{
			DecayTaskManager.getInstance().cancel(effected);
			effected.doRevive(Formulas.calculateSkillResurrectRestorePercent(_power, effector));
		}
	}
}