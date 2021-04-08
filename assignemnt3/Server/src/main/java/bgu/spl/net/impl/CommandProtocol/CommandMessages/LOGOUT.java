package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class LOGOUT extends CommandManager {

    public LOGOUT() { }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        User user =  ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user - ERROR
        if (user == null)
            return new ERROR((short) 4);

        synchronized (user){
            // the client's user is already logged out
            if (!user.isLoggedIn())
                return new ERROR((short) 4);

            // couldn't logout, another client is using this user
            if (!((CommandInvocationProtocol<Serializable>) (protocol)).nullifyUser())
                return new ERROR((short) 4);

            // raise flag - terminate
            ((CommandInvocationProtocol<Serializable>) (protocol)).flipShouldTerminate();

            return new ACK((short) 4, "\0");
        }
    }
}
