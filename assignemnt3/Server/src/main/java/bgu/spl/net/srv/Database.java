package bgu.spl.net.srv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    // key: the course line in courses.txt , value: the object course
    private ConcurrentHashMap<Integer, Course> courses;
    // key: the userName , value: the object user
    private ConcurrentHashMap<String, User> users;

    private static class SingletonHolder {
        private static Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {
        users = new ConcurrentHashMap<>();
        courses = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * loads the courses from the file path specified
     * into the Database, returns true if successful.
     */
    public void initialize(String coursesFilePath) {
        try {
            File myObj = new File(coursesFilePath);
            Scanner myReader = new Scanner(myObj);
            int index = 1;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                loadCourse(index, data);
                index++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * auxiliary function to parse data from string divided by delimiters
     * creates an add the course to courses map
     *
     * @param data the string we read from
     */
    private void loadCourse(int index, String data) {
        Course course;
        int courseNum;
        String courseName;
        ArrayList<Integer> kdamCourses = new ArrayList<>();
        int maxNumOfSeats;

        String[] splatData = data.split("\\|");

        courseNum = Integer.parseInt(splatData[0]);
        courseName = splatData[1];

        // there are kdam courses to this course - not an empty list = "[]"
        if (splatData[2].length() > 2) {

            // init kdam courses
            String tempKdamCoursesString = splatData[2].substring(1, splatData[2].length() - 1);
            String[] splatKdamCourses = tempKdamCoursesString.split(",");
            for (String splatKdamCourse : splatKdamCourses) {
                // adding the course number
                kdamCourses.add(Integer.parseInt(splatKdamCourse));
            }
        }

        maxNumOfSeats = Integer.parseInt(splatData[3]);

        //creating course
        course = new Course(courseNum, courseName, maxNumOfSeats, maxNumOfSeats, kdamCourses);

        // adding course to map by order of courses in courses file
        courses.put(index, course);
    }

    /**
     * add a user {@link User} to {@code users} HashMap
     *
     * @param userName - will be the key
     * @param user - the user to add
     * @return true if succeeded, false otherwise
     */
    public boolean addUser(String userName, User user) {
        if (user == null)
            return false;

        synchronized (user) {
            User value = users.putIfAbsent(userName, user);
            return value == null;
        }
    }


    public User getUser(String userName) {
        return users.get(userName);

    }

    /**
     * checks type of user
     *
     * @param userName to check
     * @return true if {@code userName} is a student, false otherwise
     */
    public boolean isStudent(String userName) {
        User u = users.get(userName);
        if (u == null)
            return false;
        return (u.getClass() == Student.class);
    }

    public Course getCourse (int courseNumber) {
        for (Integer key : courses.keySet()) {
            Course c = courses.get(key);
            if (c.getCourseNum() == courseNumber)
                return c;
        }
        return null;
    }

    /**
     * Aux function to sort courses list by the order in Courses file
     *
     * @param coursesToSort
     * @return the sorted list
     */
    public ArrayList<Integer> sortCourses (ArrayList<Integer> coursesToSort) {
        ArrayList<Integer> output = new ArrayList<>();
        for (int i = 1; i <= courses.size(); i++) {
            Course c = courses.get(i);
            for (Integer courseNum : coursesToSort) {
                if (c.getCourseNum() == courseNum) {
                    output.add(c.getCourseNum());
                    break;
                }
            }
        }
        return output;
    }
}
