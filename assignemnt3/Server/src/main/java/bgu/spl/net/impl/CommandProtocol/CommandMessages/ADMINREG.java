package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Admin;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class ADMINREG extends CommandManager {

    private final String userName;
    private final String password;

    public ADMINREG(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {
        Database d = Database.getInstance();

        User u = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        //this client is already logged in from a different user
        // can not perform register op
        if (u != null && u.isLoggedIn())
            return new ERROR((short) 1);

        Admin admin = new Admin(userName,password);
        if (d.addUser(userName, admin))
            return new ACK((short) 1, "\0");

        return new ERROR((short) 1);
    }
}
