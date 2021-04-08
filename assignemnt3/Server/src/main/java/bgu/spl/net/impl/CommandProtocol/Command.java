package bgu.spl.net.impl.CommandProtocol;

import bgu.spl.net.api.MessagingProtocol;

import java.io.Serializable;

public interface Command<T> extends Serializable {

    Serializable act(MessagingProtocol<Serializable> protocol);
}
