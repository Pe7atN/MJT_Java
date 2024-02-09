package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public abstract class AccountBase implements Account {

    protected String username;
    protected double balance;
    protected Course[] courses;
    protected double[] grades;
    protected int indexOfCourses;
    protected static final int maxSize = 100;

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.indexOfCourses = 0;
        this.courses = new Course[maxSize];
        this.grades = new double[maxSize];
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Its a negative value");
        } else {
            balance += amount;
        }
    }

    protected boolean alreadyInAccount(Course course) {
        for (Course crs : courses) {
            if (crs == null) {
                continue;
            }

            if (crs.equals(course)) {
                return true;
            }
        }

        return false;
    }

    private Course findCourse(Course course) {

        Course tempCourse = null;

        for (Course crs : courses) {
            if (crs == null) {
                continue;
            }
            if (crs.equals(course)) {
                tempCourse = crs;
                break;
            }
        }

        return tempCourse;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {

        if (course.getPrice() > balance) {
            throw new InsufficientBalanceException();
        }

        if (maxSize <= indexOfCourses) {
            throw new MaxCourseCapacityReachedException();
        }

        if (alreadyInAccount(course)) {
            throw new CourseAlreadyPurchasedException();
        }

        Course myCourse = new Course(course.getName(), course.getDescription(), course.getPrice(), course.getContent(), course.getCategory());
        myCourse.purchase();
        balance -= course.getPrice();
        courses[indexOfCourses++] = myCourse;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {

        if (course == null) {
            throw new IllegalArgumentException("Course is null");
        }

        if (resourcesToComplete == null) {
            throw new IllegalArgumentException("ResourcesToComplete are null");
        }

        Course tempCourse = findCourse(course);
        if (tempCourse == null) {
            throw new CourseNotPurchasedException();
        }

        for (Resource resToComplete : resourcesToComplete) {
            if (resToComplete == null) {
                continue;
            }
            boolean isFound = false;
            for (Resource courseResource : tempCourse.getContent()) {
                if (courseResource == null) {
                    continue;
                } else {
                    if (courseResource.equals(resToComplete)) {
                        tempCourse.completeResource(courseResource);
                        isFound = true;
                        break;
                    }
                }
            }
            if (!isFound) {
                throw new ResourceNotFoundException();
            }
        }

    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {

        if (grade < 2.0 || grade > 6.0) {
            throw new IllegalArgumentException("Grade is not in range [2.00, 6.00]");
        }

        if (course == null) {
            throw new IllegalArgumentException("Course is null");
        }

        Course tempCourse = findCourse(course);

        if (tempCourse == null) {
            throw new CourseNotPurchasedException();
        }

        if (tempCourse.getCompletionPercentage() != 100) {
            throw new CourseNotCompletedException();
        }

        for (int i = 0; i < indexOfCourses; i++) {
            if (courses[i].equals(tempCourse)) {
                grades[i] = grade;
                break;
            }
        }

    }

    @Override
    public Course getLeastCompletedCourse() {

        int minPercentage = 101;
        Course tempCourse = null;

        for (Course c : courses) {
            if (c == null) {
                continue;
            } else {
                if (c.getCompletionPercentage() < minPercentage) {
                    minPercentage = c.getCompletionPercentage();
                    tempCourse = c;
                }
            }
        }
        return tempCourse;
    }

    public static void main(String[] args) {
        StandardAccount st = new StandardAccount("Petar", 123);

    }

}
