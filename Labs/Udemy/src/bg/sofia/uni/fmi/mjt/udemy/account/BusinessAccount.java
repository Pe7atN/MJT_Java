package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase {

    Category[] allowedCategories;
    AccountType accountType = AccountType.BUSINESS;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = new Category[allowedCategories.length];

        int indexOfCategory = 0;
        for (Category c : allowedCategories) {
            this.allowedCategories[indexOfCategory++] = c;
        }
    }

    private boolean findCategory(Course course) {
        for (Category c : allowedCategories) {
            if (c.equals(course.getCategory())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {

        double coursePrice = (course.getPrice() - course.getPrice() * accountType.getDiscount());

        if (course.getPrice() > balance) {
            throw new InsufficientBalanceException();
        }

        if (maxSize <= indexOfCourses) {
            throw new MaxCourseCapacityReachedException();
        }

        if (alreadyInAccount(course)) {
            throw new CourseAlreadyPurchasedException();
        }

        if (!findCategory(course)) {
            throw new IllegalArgumentException("Different category");
        }

        Course myCourse = new Course(course.getName(), course.getDescription(), course.getPrice(), course.getContent(), course.getCategory());
        myCourse.purchase();
        balance -= coursePrice;
        courses[indexOfCourses++] = myCourse;
    }

}
