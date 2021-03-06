package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.AuthClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.GGAuth;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;

import java.util.Objects;

import static org.l2j.authserver.network.client.AuthClientState.AUTHED_GG;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends AuthClientPacket {

    private int _sessionId;


    @Override
    protected boolean readImpl() {
        if (available() >= 20) {
            _sessionId = readInt();
            int _data1 = readInt();
            int _data2 = readInt();
            int _data3 = readInt();
            int _data4 = readInt();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (Objects.equals(_sessionId, client.getSessionId())) {
            client.setState(AUTHED_GG);
            client.sendPacket(new GGAuth(_sessionId));
        } else {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}