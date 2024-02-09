package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Member implements GymMember, Comparable<Member> {

    private static final int DAYS_IN_WEEK = 7;
    private Address address;
    private String name;
    private int age;
    private String personalIdNumber;
    private Gender gender;
    private Map<DayOfWeek, Workout> trainingProgram;

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        this.trainingProgram = new HashMap<>(DAYS_IN_WEEK);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return Map.copyOf(trainingProgram);
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {

        if (day == null) {
            throw new IllegalArgumentException("The day is null!");
        }

        if (workout == null) {
            throw new IllegalArgumentException("The workout is null!");
        }

        trainingProgram.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {

        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("The name is null or empty!");
        }

        List<DayOfWeek> days = new ArrayList<>(DAYS_IN_WEEK);
        for (DayOfWeek day : DayOfWeek.values()) {
            if (trainingProgram.get(day) == null) {
                continue;
            }

            if (trainingProgram.get(day).exercises().getLast().name().equals(exerciseName)) {
                days.add(day);
            }

        }

        return days;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {

        if (day == null) {
            throw new IllegalArgumentException("The day is null!");
        }

        if (exercise == null) {
            throw new IllegalArgumentException("The exercises are null!");
        }

        if (trainingProgram.get(day) == null) {
            throw new DayOffException("The workout is null!");
        }

        trainingProgram.get(day).exercises().add(exercise);
    }

    public void addExercises(DayOfWeek day, List<Exercise> exercises) {

        if (day == null) {
            throw new IllegalArgumentException("The day is null!");
        }

        if (exercises == null) {
            throw new IllegalArgumentException("The exercises are null!");
        }

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("The exercises are empty !");
        }

        if (trainingProgram.get(day) == null) {
            throw new DayOffException("The workout is null!");
        }

        trainingProgram.get(day).exercises().addAll(exercises);
    }

    @Override
    public int compareTo(Member other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member member)) {
            return false;
        }

        return personalIdNumber.equals(member.personalIdNumber);
    }

    @Override
    public int hashCode() {
        return personalIdNumber.hashCode();
    }

}
