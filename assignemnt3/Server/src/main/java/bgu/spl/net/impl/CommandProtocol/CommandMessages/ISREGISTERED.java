package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class ISREGISTERED extends CommandManager {

    private final short courseNum;

    public ISREGISTERED(short courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User student = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user - ERROR
        if (student == null )
            return new ERROR((short) 7);

        synchronized (student){
            // if client's user is not logged in OR an admin - ERROR
            if (!student.isLoggedIn() || !d.isStudent(student.getUserName()))
                return new ERROR((short) 7);

            Course c = d.getCourse(courseNum);

            // check if there is such course
            if (c == null)
                return new ERROR((short) 7);

            synchronized (c){
                // check if the student has this course
                if (!((Student) student).isRegToCourse(c.getCourseNum()))
                    return new ACK((short) 9, "NOT REGISTERED" + "\0");

                return new ACK((short) 9, "REGISTERED" + "\0");
            }
        }
    }

}
