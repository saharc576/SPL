package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;

import java.io.Serializable;

public class ERROR extends CommandManager {

    private short messageOpCode;

    public ERROR(short messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    public short getMessageOpCode() { return messageOpCode; }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {
        return null;
    }
}
