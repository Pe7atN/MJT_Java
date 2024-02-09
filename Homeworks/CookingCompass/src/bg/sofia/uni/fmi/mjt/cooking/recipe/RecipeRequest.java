package bg.sofia.uni.fmi.mjt.cooking.recipe;

import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.Health;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRequest {
    private static final String SCHEME = "https://";
    private static final String AUTHORITY = "api.edamam.com";
    private static final String ENDPOINT = "/api/recipes/v2";
    private static final String START_QUERY = "?type=public";
    private static final String ID_LINK = "&app_id=";
    private static final String KEY_LINK = "&app_key=";

    private static final String KEY_WORD_LINK = "&q=";
    private static final String MEAL_TYPE_LINK = "&mealType=";
    private static final String HEALTH_LINK = "&health=";

    //required
    private final String recipeAppID;
    private final String recipeAppKey;

    //optional
    private List<String> keywords;
    private List<Health> healthLabels;
    private List<MealType> mealTypes;

    public String getRecipeAppID() {
        return recipeAppID;
    }

    public String getRecipeAppKey() {
        return recipeAppKey;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<Health> getHealthLabels() {
        return healthLabels;
    }

    public List<MealType> getMealTypes() {
        return mealTypes;
    }

    public String createRequestString() {

        StringBuilder request =
            new StringBuilder(SCHEME).append(AUTHORITY).append(ENDPOINT).append(START_QUERY).append(ID_LINK)
                .append(recipeAppID).append(KEY_LINK).append(recipeAppKey);

        for (String keyWord : keywords) {
            request.append(KEY_WORD_LINK).append(keyWord);
        }

        for (MealType meal : mealTypes) {
            request.append(MEAL_TYPE_LINK).append(meal.toString());
        }

        for (Health health : healthLabels) {
            request.append(HEALTH_LINK).append(health.toString());
        }
        return request.toString();
    }

    public static RequestBuilder builder(String recipeAppID, String recipeAppKey) {
        return new RequestBuilder(recipeAppID, recipeAppKey);
    }

    private RecipeRequest(RequestBuilder builder) {
        this.recipeAppID = builder.recipeAppID;
        this.recipeAppKey = builder.recipeAppKey;
        this.keywords = builder.keywords;
        this.healthLabels = builder.healthLabels;
        this.mealTypes = builder.mealTypes;
    }

    public static class RequestBuilder {

        //required
        private final String recipeAppID;
        private final String recipeAppKey;

        //optional
        private List<String> keywords;
        private List<Health> healthLabels;
        private List<MealType> mealTypes;

        private RequestBuilder(String recipeAppID, String recipeAppKey) {
            this.recipeAppID = recipeAppID;
            this.recipeAppKey = recipeAppKey;
            this.keywords = new ArrayList<>();
            this.healthLabels = new ArrayList<>();
            this.mealTypes = new ArrayList<>();
        }

        public RequestBuilder setKeyWords(String... keyWords) {
            this.keywords = Arrays.asList(keyWords);
            return this;
        }

        public RequestBuilder setMealTypes(MealType... mealTypes) {
            this.mealTypes = Arrays.asList(mealTypes);
            return this;
        }

        public RequestBuilder setHealthLabels(Health... healthLabels) {
            this.healthLabels = Arrays.asList(healthLabels);
            return this;
        }

        public RecipeRequest build() {
            return new RecipeRequest(this);
        }
    }
}
