package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Student extends User {

    // value = course number
    private ConcurrentLinkedQueue<Integer> courses;

    public Student(String userName, String password) {
        super(userName, password);
        courses = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<Integer> getCourses() {
        return courses;
    }

    /**
     * add a course to this student's courses
     * and take a seat in course
     * @param course to add
     * @return true if succeeded, false otherwise
     */
    public boolean addCourse(Course course) {
        if (course == null)
            return false;
        if (courses.contains(course.getCourseNum()))
            return false;
        if (!course.releaseOrTakeAseat(1))
            return false;
        courses.add(course.getCourseNum());
        return true;
    }

    /**
     * remove a course from this student's courses
     * and release a seat in course
     * @param course to remove
     * @return true if succeeded, false otherwise
     */
    public boolean removeCourse(Course course) {
        if (course == null)
            return false;
        if (!courses.contains(course.getCourseNum()))
            return false;
        if (!course.releaseOrTakeAseat(-1))
            return false;
        courses.remove(course.getCourseNum());
        return true;
    }

    /**
     * check if the student ha all needed kdam courses
     * @param kdamCoursesNeeded Array list of the courses numbers
     * @return true if succeeded, false otherwise
     */
    public boolean passedKdamCourses(ArrayList<Integer> kdamCoursesNeeded) {
        for (Integer kdamCourse : kdamCoursesNeeded) {
            if (!courses.contains(kdamCourse))
                return false;
        }
        return true;
    }

    public boolean isRegToCourse(int courseNum) {
        return courses.contains(courseNum);
    }
}
