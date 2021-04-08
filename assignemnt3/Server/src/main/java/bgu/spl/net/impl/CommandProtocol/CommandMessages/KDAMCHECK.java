package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.io.Serializable;
import java.util.ArrayList;

public class KDAMCHECK extends CommandManager {

    private final short courseNum;

    public KDAMCHECK(short courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User student = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user - ERROR
        if (student == null )
            return new ERROR((short) 6);

        synchronized (student){
            // if client's user is not logged in OR an admin - ERROR
            if (!student.isLoggedIn() || !d.isStudent(student.getUserName()))
                return new ERROR((short) 6);

            Course c = d.getCourse(courseNum);

            if (c == null)
                return new ERROR((short) 6);

            synchronized (c){
                // get the Kdam courses and sort them by file order
                ArrayList<Integer> kdamCourses = d.sortCourses(c.getKdamCourses());

                // change the list to String and remove spaces
                String listKdam = kdamCourses.toString();
                listKdam = listKdam.replace(" ", "");

                return new ACK((short) 6, listKdam + "\0");
            }
        }
    }
}
