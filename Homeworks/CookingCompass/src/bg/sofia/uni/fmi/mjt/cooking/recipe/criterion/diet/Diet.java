package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet;

public enum Diet {
    BALANCED("balanced"),
    HIGH_FIBER("high-fiber"),
    HIGH_PROTEIN("high-protein"),
    LOW_CARB("low-carb"),
    LOW_FAT("low-fat"),
    LOW_SODIUM("low-sodium");

    private final String value;

    Diet(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

}
