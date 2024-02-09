package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.*;


public class EducationalAccount extends AccountBase {

    AccountType accountType = AccountType.EDUCATION;
    private boolean coursesUsedForDiscount[];

    public EducationalAccount(String username, double balance) {
        super(username, balance);
        this.coursesUsedForDiscount = new boolean[maxSize];
    }

    private boolean getDiscountOf() {

        boolean getDiscount = false;
        if (indexOfCourses >= 5) {
            double avgGrade = 0;
            for (int j = indexOfCourses - 5; j < indexOfCourses; j++) {
                if (!coursesUsedForDiscount[j] && grades[j] != 0) {
                    avgGrade += grades[j];
                } else {
                    return false;
                }
            }

            if (avgGrade / 5 >= 4.50) {
                getDiscount = true;
            }

            if (getDiscount) {
                for (int i = indexOfCourses - 5; i < indexOfCourses; i++) {
                    coursesUsedForDiscount[i] = true;
                }
            }
        }

        return getDiscount;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        if (maxSize <= indexOfCourses) {
            throw new MaxCourseCapacityReachedException();
        }

        if (alreadyInAccount(course)) {
            throw new CourseAlreadyPurchasedException();
        }

        double coursePrice = price(course);

        if (coursePrice > balance) {
            throw new InsufficientBalanceException();
        }

        Course myCourse = new Course(course.getName(), course.getDescription(), course.getPrice(), course.getContent(), course.getCategory());
        myCourse.purchase();
        balance -= coursePrice;
        courses[indexOfCourses++] = myCourse;
    }

    private double price(Course course) {
        return course.getPrice() - (!getDiscountOf() ? 0 : course.getPrice() * accountType.getDiscount());
    }
}

