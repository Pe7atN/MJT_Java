package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;


public class Resource implements Completable {

    private String name;
    private ResourceDuration duration;
    private boolean isCompleted;

    public Resource(String name, ResourceDuration duration) {
        this.name = name;
        this.duration = duration;
        isCompleted = false;
    }

    public String getName() {
        return name;
    }

    public ResourceDuration getDuration() {
        return duration;
    }

    public void complete() {
        isCompleted = true;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public int getCompletionPercentage() {
        if (isCompleted) {
            return 100;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        Resource that = (Resource) o;
        return name.equals(that.name) &&
                duration.equals(that.duration);
    }

}
