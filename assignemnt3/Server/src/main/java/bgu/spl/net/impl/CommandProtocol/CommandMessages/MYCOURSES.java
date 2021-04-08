package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;
import java.util.ArrayList;

public class MYCOURSES extends CommandManager {

    public MYCOURSES() {
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User student = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user - ERROR
        if (student == null)
            return new ERROR((short) 7);

        synchronized (student){
            // if client's user is not logged in OR an admin - ERROR
            if (!student.isLoggedIn() || !d.isStudent(student.getUserName()))
                return new ERROR((short) 7);

            // get the relevant courses and sort them by file order

            ArrayList<Integer> courses = new ArrayList<>(((Student) student).getCourses());
            courses = d.sortCourses(courses);

            // change the list to String and remove spaces
            String coursesList = courses.toString();
            coursesList = coursesList.replace(" ", "");

            return new ACK((short) 8, coursesList + "\0");
        }
    }
}
