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

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Block;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Flip Block effect implementation.
 * @author Mobius
 */
public final class FlipBlock extends AbstractEffect
{
	public FlipBlock(StatsSet params)
	{
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		final Block block = effected instanceof Block ? (Block) effected : null;
		final Player player = isPlayer(effector) ? (Player) effector : null;
		if ((block == null) || (player == null))
		{
			return;
		}
		
		final int arena = player.getBlockCheckerArena();
		if (arena != -1)
		{
			final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);
			if (holder == null)
			{
				return;
			}
			
			final int team = holder.getPlayerTeam(player);
			final int color = block.getColorEffect();
			if ((team == 0) && (color == 0x00))
			{
				block.changeColor(player, holder, team);
			}
			else if ((team == 1) && (color == 0x53))
			{
				block.changeColor(player, holder, team);
			}
		}
	}
}