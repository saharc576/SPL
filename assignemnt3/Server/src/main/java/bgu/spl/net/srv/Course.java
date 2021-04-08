package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.TreeSet;

public class Course {

    private final int courseNum;
    private final String courseName;
    private final int maxNumOfSeats;
    private int numOfSeatsAvailable;
    private final ArrayList<Integer> kdamCourses;
    private TreeSet<String> students;

    public Course(int courseNum, String courseName, int maxNumOfSeats, int numOfSeatsAvailable, ArrayList<Integer> kdamCourses) {
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.maxNumOfSeats = maxNumOfSeats;
        this.numOfSeatsAvailable = numOfSeatsAvailable;
        this.kdamCourses = kdamCourses;
        students = new TreeSet<>();
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getMaxNumOfSeats() {
        return maxNumOfSeats;
    }

    public int getNumOfSeatsAvailable() {
        return numOfSeatsAvailable;
    }

    public synchronized boolean releaseOrTakeAseat(int seatsToReleaseOrTake) {
        // someone wants to take a seat and there are available seats
        if (numOfSeatsAvailable > 0 && seatsToReleaseOrTake > 0)
            this.numOfSeatsAvailable -= seatsToReleaseOrTake;

        // someone wants to release a seat but he doesn't have one
        else if(numOfSeatsAvailable < maxNumOfSeats && seatsToReleaseOrTake < 0)
            this.numOfSeatsAvailable -= seatsToReleaseOrTake;

        else // neither of these cases - illegal
            return false;

        return true;
    }

    public ArrayList<Integer> getKdamCourses() {
        return kdamCourses;
    }

    public TreeSet<String> getStudents() {
        return students;
    }

    public boolean addStudent(Student studentToAdd) {
        return students.add(studentToAdd.getUserName());
    }

    public void removeStudent(Student studentToRemove) {
        students.remove(studentToRemove.getUserName());
    }

}
