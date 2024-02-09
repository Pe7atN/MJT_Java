package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype;

public enum MealType {
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH_DINNER("lunch/dinner"),
    SNACK("snack"),
    TEATIME("teatime");

    private final String value;

    MealType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

}
