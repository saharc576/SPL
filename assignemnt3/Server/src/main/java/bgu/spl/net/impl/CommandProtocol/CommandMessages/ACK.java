package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;

import java.io.Serializable;

public class ACK extends CommandManager {

    private short messageOpCode;
    private String optional;

    public ACK(short messageOpCode, String optional) {
        this.messageOpCode = messageOpCode;
        this.optional = optional;
    }

    public short getMessageOpCode() { return messageOpCode; }

    public String getOptional() { return optional;}

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {
        return null;
    }
}
