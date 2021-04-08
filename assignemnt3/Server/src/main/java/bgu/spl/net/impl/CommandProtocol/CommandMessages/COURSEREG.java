package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Student;
import bgu.spl.net.srv.User;

import java.io.Serializable;
import java.util.ArrayList;

public class COURSEREG extends CommandManager {

    private final short courseNum;

    public COURSEREG(short courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {
        Database d = Database.getInstance();
        User student = ((CommandInvocationProtocol<Serializable>) (protocol)).getLoggedInUser();

        // this client is not connected from any user - ERROR
        if (student == null) {
            return new ERROR((short) 5);
        }

        Course c = d.getCourse(courseNum);

        // check if there is such course
        if (c == null)
            return new ERROR((short) 5);

        synchronized (student){
            // student is not logged in OR an admin - ERROR
            if (!student.isLoggedIn() || !d.isStudent(student.getUserName())) {
                return new ERROR((short) 5);
            }
            synchronized (c) {
                // check if there are free seats
                if (c.getNumOfSeatsAvailable() <= 0)
                    return new ERROR((short) 5);

                ArrayList<Integer> kdamCoursesNeeded = c.getKdamCourses();

                // check if the student has all kdam courses
                if (!((Student) student).passedKdamCourses(kdamCoursesNeeded))
                    return new ERROR((short) 5);

                // adding student to course
                if (!c.addStudent((Student) student))
                    return new ERROR((short) 5);

                // adding course to student
                if (!((Student) student).addCourse(c))
                    return new ERROR((short) 5);

                return new ACK((short) 5, "\0");
            }
        }
    }
}
