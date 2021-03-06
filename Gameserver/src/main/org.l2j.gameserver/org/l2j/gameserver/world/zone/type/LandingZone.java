package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A landing zone
 *
 * @author Kerberos
 */
public class LandingZone extends Zone {
    public LandingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.LANDING, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.LANDING, false);
        }
    }
}
