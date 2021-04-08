package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;
import java.util.ArrayList;

public class STUDENTSTAT extends CommandManager {
    private final String userName;

    public STUDENTSTAT(String userName) {
        this.userName = userName;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User admin = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user
        // or a mot an admin - ERROR
        if (admin == null || !admin.isLoggedIn() || d.isStudent(admin.getUserName()))
            return new ERROR((short) 8);

        Student student = (Student) d.getUser(userName);

        // there is no such student
        if (student == null )
            return new ERROR((short) 8);

        synchronized (student){
            if (!d.isStudent(userName))
                return new ERROR((short) 8);

            String output = "";

            output += "Student: " + student.getUserName() + "\n";

            // get the relevant courses and sort them by file order
            ArrayList<Integer> courses = new ArrayList<>(student.getCourses());
            courses = d.sortCourses(courses);

            // change the list to String and remove spaces
            String coursesList = courses.toString();
            coursesList = coursesList.replace(" ", "");

            output += "Courses: " + coursesList;

            return new ACK((short) 8, output + "\0");
        }
    }
}
