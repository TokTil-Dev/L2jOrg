package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExUISetting;

/**
 * @author KenM / mrTJO
 */
public class RequestKeyMapping extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (Config.STORE_UI_SETTINGS) {
            client.sendPacket(new ExUISetting(activeChar));
        }
    }
}
