package org.l2j.gameserver.network.clientpackets;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packets received by the game server from clients
 *
 * @author KenM
 */
public abstract class IClientIncomingPacket extends ReadablePacket<L2GameClient> {

    private static Logger LOGGER = LoggerFactory.getLogger(IClientIncomingPacket.class.getName());

    @Override
    protected boolean read() {
        try {
            readImpl();
            return true;
        } catch (InvalidDataPacketException e) {
            LOGGER.warn("[{}] Invalid data packet {} from client {}", GameServer.fullVersion, this, client);
        } catch (Exception e) {
            LOGGER.error("[{}] Error while reading packet {} from client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("[{}] Error while running packet {} from client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    protected abstract void runImpl() throws Exception;

    protected abstract void readImpl() throws Exception;
}
