package bgu.spl.net.impl.CommandProtocol.CommandMessages;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.CommandProtocol.Command;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.io.Serializable;

public class COURSESTAT extends CommandManager {

    private final short courseNum;

    public COURSESTAT(short courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public Command<Serializable> act(MessagingProtocol<Serializable> protocol) {

        Database d = Database.getInstance();
        User admin = ((CommandInvocationProtocol<Serializable>)(protocol)).getLoggedInUser();

        // if loggedInUser is null, he never logged in OR a student - ERROR
        if (admin == null || !admin.isLoggedIn() || d.isStudent(admin.getUserName()))
            return new ERROR((short) 7);

        Course c = d.getCourse(courseNum);

        // check if there is such course
        if (c == null)
            return new ERROR((short) 7);

        synchronized (c){
            String output = "";

            output += "Course: (" + c.getCourseNum() + ") " + c.getCourseName() + "\n";
            output += "Seats Available: " + c.getNumOfSeatsAvailable() + "/" + c.getMaxNumOfSeats() + "\n";
            output += "Students Registered: " + c.getStudents().toString().replace(" ", "");

            return new ACK((short) 7, output + "\0");
        }
    }
}
