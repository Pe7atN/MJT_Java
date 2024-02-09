package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.sql.SQLOutput;
import java.util.Arrays;

public class Course implements Completable, Purchasable {

    private String name;
    private String description;
    private double price;
    private Resource[] content;
    private Category category;

    private boolean isPurchased;

    public Course(String name, String description, double price, Resource[] content, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = new Resource[content.length];
        int indexOfContent = 0;
        for (Resource r : content) {
            this.content[indexOfContent++] = r;
        }
        this.category = category;
        this.isPurchased = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Resource[] getContent() {
        return content;
    }

    public CourseDuration getTotalTime() {
        return CourseDuration.of(content);
    }

    public Category getCategory() {
        return category;
    }

    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {

        for (Resource r : content) {
            if (r.equals(resourceToComplete)) {
                r.complete();
                return;
            }
        }

        throw new ResourceNotFoundException();
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
    }

    @Override
    public boolean isCompleted() {
        for (Resource res : content) {
            if (!res.isCompleted()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getCompletionPercentage() {

        if(content.length == 0){
            return 100;
        }

        int completedCount = 0;

        for (Resource r : content) {
            if (r.isCompleted()) {
                completedCount++;
            }
        }

        return (int) Math.round(completedCount * 100.0 / content.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        Course that = (Course) o;
        return name.equals(that.name) &&
                description.equals(that.description) &&
                price == that.price &&
                Arrays.equals(content, that.content) &&
                category.equals(that.category);
    }

}