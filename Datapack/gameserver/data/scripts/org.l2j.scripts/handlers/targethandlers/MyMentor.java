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
package handlers.targethandlers;

import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.TargetType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Target my mentor.
 * @author Nik
 */
public class MyMentor implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.MY_MENTOR;
	}
	
	@Override
	public WorldObject getTarget(Creature activeChar, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		if (isPlayer(activeChar))
		{
			final Mentee mentor = MentorManager.getInstance().getMentor(activeChar.getObjectId());
			if (mentor != null)
			{
				return mentor.getPlayerInstance();
			}
		}
		
		return null;
	}
}
