package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;

public class Udemy implements LearningPlatform {

    private Account[] accounts;
    private int indexOfAccounts;
    private Course[] courses;
    private int indexOfCourses;

    public Udemy(Account[] accounts, Course[] courses) {
        this.indexOfAccounts = 0;
        this.indexOfCourses = 0;
        this.accounts = new Account[accounts.length];
        this.courses = new Course[courses.length];

        for (Account acc : accounts) {
            this.accounts[indexOfAccounts++] = acc;
        }

        for (Course crs : courses) {
            this.courses[indexOfCourses++] = crs;
        }

    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {

        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name is blank");
        }

        for (Course crs : courses) {
            if (crs == null) {
                continue;
            }

            if (crs.getName().equals(name))
                return crs;
        }

        throw new CourseNotFoundException();
    }

    @Override
    public Course[] findByKeyword(String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("Keyword is null");
        }

        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Keyword is blank");
        }

        char[] charactersOfKeyWord = keyword.toCharArray();

        for (Character c : charactersOfKeyWord) {
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException("Keyword contains special characters");
            }
        }

        int countCrs = 0;
        for (Course crs : courses) {
            if (crs.getName().contains(keyword) || crs.getDescription().contains(keyword)) {
                countCrs++;
            }
        }

        Course[] tempCourses = new Course[countCrs];
        countCrs = 0;
        for (Course crs : courses) {
            if (crs.getName().contains(keyword) || crs.getDescription().contains(keyword)) {
                tempCourses[countCrs++] = crs;
            }
        }

        return tempCourses;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {

        if (category == null) {
            throw new IllegalArgumentException("Category is null");
        }

        int countCrs = 0;
        for (Course crs : courses) {
            if (crs == null) {
                continue;
            }

            if (crs.getCategory().equals(category)) {
                countCrs++;
            }
        }

        Course[] tempCourses = new Course[countCrs];
        countCrs = 0;
        for (Course crs : courses) {
            if (crs == null) {
                continue;
            }

            if (crs.getCategory().equals(category)) {
                tempCourses[countCrs++] = crs;
            }
        }

        return tempCourses;
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name is blank");
        }

        for (Account acc : accounts) {
            if (acc == null) {
                continue;
            }

            if (acc.getUsername().equals(name))
                return acc;
        }

        throw new AccountNotFoundException();
    }

    @Override
    public Course getLongestCourse() {

        int maxDuration = 0;
        Course tempCourse = null;

        for (Course crs : courses) {

            int currentDuration = crs.getTotalTime().hours() * 60 + crs.getTotalTime().minutes();
            if (currentDuration > maxDuration) {
                maxDuration = currentDuration;
                tempCourse = crs;
            }
        }

        return tempCourse;
    }

    @Override
    public Course getCheapestByCategory(Category category) {

        if (category == null) {
            throw new IllegalArgumentException("Category is null");
        }

        Course[] coursesByCat = getAllCoursesByCategory(category);
        if (coursesByCat.length == 0) {
            return null;
        }

        Course tempCourse = coursesByCat[0];
        for (int i = 1; i < coursesByCat.length; i++) {
            if (tempCourse.getPrice() > coursesByCat[i].getPrice()) {
                tempCourse = coursesByCat[i];
            }
        }

        return tempCourse;
    }
}
