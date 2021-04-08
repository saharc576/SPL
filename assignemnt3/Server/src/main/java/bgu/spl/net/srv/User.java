package bgu.spl.net.srv;

/**
 * abstract class that all users are derived from
 */
public abstract class User {

    private final String userName;
    private final String password;
    private boolean isLoggedIn;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.isLoggedIn = false;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public synchronized boolean isLoggedIn() {
        return isLoggedIn;
    }

    public synchronized void login() {
        isLoggedIn = true;
    }
    public synchronized void logout() {
        isLoggedIn = false;
    }

}
