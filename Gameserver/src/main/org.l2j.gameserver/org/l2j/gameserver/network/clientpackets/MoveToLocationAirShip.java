package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.instancemanager.AirShipManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.model.actor.instance.AirShip;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public class MoveToLocationAirShip extends ClientPacket {
    public static final int MIN_Z = -895;
    public static final int MAX_Z = 6105;
    public static final int STEP = 300;

    private int _command;
    private int _param1;
    private int _param2 = 0;

    @Override
    public void readImpl() {
        _command = readInt();
        _param1 = readInt();
        if (available() > 0) {
            _param2 = readInt();
        }
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.isInAirShip()) {
            return;
        }

        final AirShip ship = activeChar.getAirShip();
        if (!ship.isCaptain(activeChar)) {
            return;
        }

        int z = ship.getZ();

        switch (_command) {
            case 0: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (_param1 < World.GRACIA_MAX_X) {
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_param1, _param2, z));
                }
                break;
            }
            case 1: {
                if (!ship.canBeControlled()) {
                    return;
                }
                ship.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                break;
            }
            case 2: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (z < World.GRACIA_MAX_Z) {
                    z = Math.min(z + STEP, World.GRACIA_MAX_Z);
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(ship.getX(), ship.getY(), z));
                }
                break;
            }
            case 3: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (z > World.GRACIA_MIN_Z) {
                    z = Math.max(z - STEP, World.GRACIA_MIN_Z);
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(ship.getX(), ship.getY(), z));
                }
                break;
            }
            case 4: {
                if (!ship.isInDock() || ship.isMoving()) {
                    return;
                }

                final VehiclePathPoint[] dst = AirShipManager.getInstance().getTeleportDestination(ship.getDockId(), _param1);
                if (dst == null) {
                    return;
                }

                // Consume fuel, if needed
                final int fuelConsumption = AirShipManager.getInstance().getFuelConsumption(ship.getDockId(), _param1);
                if (fuelConsumption > 0) {
                    if (fuelConsumption > ship.getFuel()) {
                        activeChar.sendPacket(SystemMessageId.YOUR_AIRSHIP_CANNOT_TELEPORT_BECAUSE_DUE_TO_LOW_FUEL);
                        return;
                    }
                    ship.setFuel(ship.getFuel() - fuelConsumption);
                }

                ship.executePath(dst);
                break;
            }
        }
    }
}
