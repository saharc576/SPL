package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;

import java.io.Serializable;

/**
 * An abstract class that all are commands derived from
 */
public abstract class CommandManager implements Command<Serializable> {

    @Override
    public abstract Command<Serializable> act (MessagingProtocol<Serializable> protocol);
}
