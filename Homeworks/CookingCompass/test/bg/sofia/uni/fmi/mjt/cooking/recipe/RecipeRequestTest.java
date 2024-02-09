package bg.sofia.uni.fmi.mjt.cooking.recipe;

import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.Health;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipeRequestTest {

    @Test
    void testBuilderWithDefaultValues() {
        RecipeRequest request = RecipeRequest.builder("appID", "appKey").build();

        assertEquals("appID", request.getRecipeAppID(),
            "It was expected to be the same appID");
        assertEquals("appKey", request.getRecipeAppKey(),
            "It was expected to be the same appKey");
        assertEquals(0, request.getKeywords().size(), "It was expected the size to be zero");
        assertEquals(0, request.getMealTypes().size(), "It was expected the size to be zero");
        assertEquals(0, request.getHealthLabels().size(), "It was expected the size to be zero");
    }

    @Test
    void testBuilderWithSetValues() {
        RecipeRequest request = RecipeRequest.builder("appID", "appKey").setKeyWords("pasta", "tomato")
            .setMealTypes(MealType.BREAKFAST, MealType.LUNCH_DINNER).setHealthLabels(Health.VEGAN).build();

        assertEquals("appID", request.getRecipeAppID(), "It was expected to be the same appID");
        assertEquals("appKey", request.getRecipeAppKey(), "It was expected to be the same appKey");
        assertEquals(2, request.getKeywords().size(), "It was expected to be the same size");
        assertTrue(request.getKeywords().contains("pasta"), "It was expected the list to contain pasta");
        assertTrue(request.getKeywords().contains("tomato"), "It was expected the list to contain tomato");
        assertEquals(2, request.getMealTypes().size(), "It was expected to be the same size");
        assertTrue(request.getMealTypes().contains(MealType.BREAKFAST), "It was expected to contain breakfast");
        assertTrue(request.getMealTypes().contains(MealType.LUNCH_DINNER), "It was expected to contain lunch/dinner");
        assertEquals(1, request.getHealthLabels().size(), "It was expected to be the same size");
        assertTrue(request.getHealthLabels().contains(Health.VEGAN), "It was expected to contain vegan");
    }

    @Test
    void testRequestString() {
        RecipeRequest request =
            RecipeRequest.builder("CUSTOM_APP_ID", "CUSTOM_APP_KEY").setKeyWords("pasta", "tomato")
                .setHealthLabels(Health.VEGAN).setMealTypes(MealType.BREAKFAST).build();
        String stringRequest =
            "https://api.edamam.com/api/recipes/v2?type=public&app_id=CUSTOM_APP_ID&app_key=CUSTOM_APP_KEY&q=pasta&q=tomato&mealType=breakfast&health=vegan";

        assertEquals(stringRequest, request.createRequestString(),
            "It was expected to be the same request String");

    }
}

