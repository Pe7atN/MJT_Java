package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {

    public double getDistanceTo(Address other) {
        double dx = Math.abs(this.longitude - other.longitude);
        double dy = Math.abs(this.latitude - other.latitude);

        double dist = dx * dx + dy * dy;
        return Math.sqrt(dist);
    }
}
