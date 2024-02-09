package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {

    public static CourseDuration of(Resource[] content){

        int totalTime = 0;
        for(Resource c : content){
            if(c == null){
                continue;
            }
            totalTime += c.getDuration().minutes();
        }

        return new CourseDuration(totalTime / 60, totalTime % 60);
    }

    public CourseDuration{

        if(hours < 0 || hours > 24){
            throw new IllegalArgumentException("Minutes must be between 0 and 60");
        }

        if(minutes < 0 || minutes > 60){
            throw new IllegalArgumentException("Minutes must be between 0 and 60");
        }
    }


}
