package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class LOGIN extends CommandManager {

    private final String userName;
    private final String password;

    public LOGIN(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User user = d.getUser(userName);

        // there is no such user
        if (user == null )
            return new ERROR((short) 3);

        synchronized (user){
            // user is already logged in or wrong password
            if (user.isLoggedIn() || !user.getPassword().equals(password))
                return new ERROR((short) 3);

            // couldn't login, another client is using this user
            if (!((CommandInvocationProtocol<Serializable>) (protocol)).setLoggedInUser(user))
                return new ERROR((short) 3);

            return new ACK((short) 3, "\0");
        }
    }
}
