package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class UNREGISTER extends CommandManager{
    private final short courseNum;

    public UNREGISTER(short courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User student = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // the client is not logged in from any user - ERROR
        if (student == null )
            return new ERROR((short) 7);

        Course c = d.getCourse(courseNum);

        // there is no such course
        if (c == null)
            return new ERROR((short) 7);

        synchronized (student){
            // if client's user is not logged in OR an admin - ERROR
            if (!student.isLoggedIn() || !d.isStudent(student.getUserName()))
                return new ERROR((short) 7);

            synchronized (c) {
                // removing course from student courses
                // & removing student from course's list
                if (!((Student) student).removeCourse(c))
                    return new ERROR((short) 7);
                c.removeStudent((Student) student);

                return new ACK((short) 9, "\0");
            }
        }
    }
}
