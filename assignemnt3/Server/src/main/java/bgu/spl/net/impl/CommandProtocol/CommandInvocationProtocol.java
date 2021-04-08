package bgu.spl.net.impl.CommandProtocol;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.CommandMessages.*;
import bgu.spl.net.srv.*;

import java.io.Serializable;

public class CommandInvocationProtocol<T> implements MessagingProtocol<Serializable> {
    private User loggedInUser;
    private boolean shouldTerminate = false;

    public CommandInvocationProtocol() {
        this.loggedInUser = null;
    }

    @Override
    public Serializable process(Serializable msg) {
        CommandManager c = (CommandManager) msg;
        return c.act(this);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void flipShouldTerminate() {
        shouldTerminate = true;
    }

    public synchronized User getLoggedInUser() {
        return loggedInUser;
    }

    public synchronized boolean setLoggedInUser(User loggedInUser) {
        if (loggedInUser == null)
            return false;

        // checking if there is a client using this user
        if (this.loggedInUser != null && this.loggedInUser.isLoggedIn())
            return false;

        this.loggedInUser = loggedInUser;
        loggedInUser.login();
        return true;
    }

    public synchronized boolean nullifyUser() {
        if (this.loggedInUser == null)
            return false;

        this.loggedInUser.logout();
        this.loggedInUser = null;
        return true;
    }


}
