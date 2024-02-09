package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;

import java.time.DayOfWeek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {
    private int capacity;
    private Address address;
    private SortedSet<GymMember> gymMembers;

    public Gym(int capacity, Address address) {
        this.capacity = capacity;
        this.address = address;
        this.gymMembers = new TreeSet<>();
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return Collections.unmodifiableSortedSet(gymMembers);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        SortedSet<GymMember> sortedByName = new TreeSet<>(new GymByNameComparator());
        sortedByName.addAll(gymMembers);

        return Collections.unmodifiableSortedSet(sortedByName);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        SortedSet<GymMember> sortedByProximity = new TreeSet<>(new GymByProximityComparator(address));
        sortedByProximity.addAll(gymMembers);

        return Collections.unmodifiableSortedSet(sortedByProximity);
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {

        if (gymMembers.size() >= capacity) {
            throw new GymCapacityExceededException("The gym is full!");
        }

        if (member == null) {
            throw new IllegalArgumentException("The member is null");
        }

        gymMembers.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {

        if (members == null) {
            throw new IllegalArgumentException("The member is null!");
        }

        if (members.isEmpty()) {
            throw new IllegalArgumentException("The members is empty!");
        }

        if (gymMembers.size() + members.size() >= capacity) {
            throw new GymCapacityExceededException("The gym is full!");
        }

        gymMembers.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {

        if (member == null) {
            throw new IllegalArgumentException("The member is null");
        }

        for (GymMember mem : gymMembers) {

            if (mem.getPersonalIdNumber().equals(member.getPersonalIdNumber())) {
                return true;
            }

        }

        return false;
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {

        if (day == null) {
            throw new IllegalArgumentException("The day is null!");
        }

        if (exerciseName == null) {
            throw new IllegalArgumentException("The exercise is null!");
        }

        if (exerciseName.isEmpty()) {
            throw new IllegalArgumentException("The exercise is empty!");
        }

        for (GymMember members : gymMembers) {
            if (members.getTrainingProgram().get(day) == null) {
                continue;
            }

            for (Exercise exercise : members.getTrainingProgram().get(day).exercises()) {
                if (exercise.name().equals(exerciseName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("The exercise is null!");
        }

        if (exerciseName.isEmpty()) {
            throw new IllegalArgumentException("The exercise is empty!");
        }

        Map<DayOfWeek, List<String>> listOfMembers = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {

            for (GymMember members : gymMembers) {
                if (members.getTrainingProgram().get(day) == null) {
                    continue;
                }

                for (Exercise exercise : members.getTrainingProgram().get(day).exercises()) {

                    if (exercise.name().equals(exerciseName)) {
                        listOfMembers.putIfAbsent(day, new ArrayList<>());
                        listOfMembers.get(day).add(members.getName());
                    }
                }
            }
        }

        return Map.copyOf(listOfMembers);
    }

    private static class GymByNameComparator implements Comparator<GymMember> {

        @Override
        public int compare(GymMember member1, GymMember member2) {
            return member1.getName().compareTo(member2.getName());
        }
    }

    private static class GymByProximityComparator implements Comparator<GymMember> {
        private Address gymAddress;

        public GymByProximityComparator(Address gymAddress) {
            this.gymAddress = gymAddress;
        }

        @Override
        public int compare(GymMember member1, GymMember member2) {
            double distance1 = member1.getAddress().getDistanceTo(gymAddress);
            double distance2 = member2.getAddress().getDistanceTo(gymAddress);

            return Double.compare(distance1, distance2);
        }
    }
}


