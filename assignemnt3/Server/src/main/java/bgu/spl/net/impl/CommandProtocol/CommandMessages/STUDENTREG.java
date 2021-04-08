package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class STUDENTREG extends CommandManager {
    private final String userName;
    private final String password;

    public STUDENTREG(String userName, String password) {
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
            return new ERROR((short) 2);

        Student student = new Student(userName, password);
        if (d.addUser(userName, student))
            return new ACK((short) 2, "\0");

        return new ERROR((short) 2);
    }

}
