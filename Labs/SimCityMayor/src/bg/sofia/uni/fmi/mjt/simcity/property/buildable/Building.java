package bg.sofia.uni.fmi.mjt.simcity.property.buildable;

public class Building implements Buildable {

    private BuildableType type;
    private int area;

    public Building(BuildableType type, int area) {
        this.type = type;
        this.area = area;
    }

    public BuildableType getType() {
        return type;
    }

    public int getArea() {
        return area;
    }

}
