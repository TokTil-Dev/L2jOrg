package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * ConfirmDlg server packet implementation.
 *
 * @author kombat, UnAfraid
 */
public class ConfirmDlg extends AbstractMessagePacket<ConfirmDlg> {
    private int _time;
    private int _requesterId;

    public ConfirmDlg(SystemMessageId smId) {
        super(smId);
    }

    public ConfirmDlg(String text) {
        this(SystemMessageId.S1);
        addString(text);
    }

    public ConfirmDlg addTime(int time) {
        _time = time;
        return this;
    }

    public ConfirmDlg addRequesterId(int id) {
        _requesterId = id;
        return this;
    }

    @Override
    protected void writeParamsSize(int size) {
        writeInt(size);
    }

    @Override
    protected void writeParamType(int type) {
        writeInt(type);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CONFIRM_DLG);

        writeInt(getId());
        writeMe();
        writeInt(_time);
        writeInt(_requesterId);
    }

}
