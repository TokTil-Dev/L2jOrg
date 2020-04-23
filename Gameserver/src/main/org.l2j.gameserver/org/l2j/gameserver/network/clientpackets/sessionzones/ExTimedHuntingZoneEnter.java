package org.l2j.gameserver.network.clientpackets.sessionzones;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author Mobius
 */
public class ExTimedHuntingZoneEnter extends ClientPacket {
	private int _zoneId;

	@Override
	public void readImpl()
	{
		_zoneId = readInt();
	}

	@Override
	public void runImpl()
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}

		if (player.isMounted())
		{
			player.sendMessage("Cannot use time-limited hunting zones while mounted.");
			return;
		}
		if (player.isInDuel())
		{
			player.sendMessage("Cannot use time-limited hunting zones during a duel.");
			return;
		}
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("Cannot use time-limited hunting zones while waiting for the Olympiad.");
			return;
		}
		if (player.isOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
			return;
		}
		if (player.isInInstance())
		{
			player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
			return;
		}

		if ((_zoneId == 2) && (player.getLevel() < 78))
		{
			player.sendMessage("Your level does not correspond the zone equivalent.");
		}

		final long currentTime = System.currentTimeMillis();
		long endTime = player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_RESET_TIME + _zoneId, 0);
		if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime)
		{
			endTime = currentTime + Config.TIME_LIMITED_ZONE_INITIAL_TIME;
		}

		if (endTime > currentTime)
		{
			if (player.getAdena() > Config.TIME_LIMITED_ZONE_TELEPORT_FEE)
			{
				player.reduceAdena("TimedHuntingZone", Config.TIME_LIMITED_ZONE_TELEPORT_FEE, player, true);
			}
			else
			{
				player.sendMessage("Not enough adena.");
				return;
			}

			switch (_zoneId)
			{
				case 2: // Ancient Pirates' Tomb
				{
					player.teleToLocation(17613, -76862, -6265);
					break;
				}
			}

			player.getVariables().set(PlayerVariables.HUNTING_ZONE_RESET_TIME + _zoneId, endTime);
			player.startTimedHuntingZone(_zoneId, endTime - currentTime);
		}
		else
		{
			player.sendMessage("You don't have enough time available to enter the hunting zone.");
		}
	}
}
