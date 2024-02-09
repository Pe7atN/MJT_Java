package bg.sofia.uni.fmi.mjt.gym.workout;

public record Exercise(String name, int sets, int repetitions) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exercise exercise)) {
            return false;
        }

        return name.equals(exercise.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
