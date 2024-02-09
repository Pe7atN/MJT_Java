package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.exception.UnauthorizedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.recipe.RecipeRequest;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.cuisine.CuisineType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet.Diet;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.dishtype.DishType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.Health;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CookingCompassTest {

    CookingCompass cookingCompass = new CookingCompass();

    @Test
    void testSearchRecipes() throws Exception{
        RecipeRequest recipeRequest = RecipeRequest.builder("YOUR_ID", "YOUR_KEY").setKeyWords("chocolate").setHealthLabels(
            Health.EGG_FREE).setMealTypes(MealType.BREAKFAST).build();
        List<Recipe> recipes = cookingCompass.searchRecipes(recipeRequest,4);

        Recipe recipe = new Recipe(
            "Chocolate-Covered Matzo",
            List.of(Diet.LOW_SODIUM),
            Arrays.asList(
                Health.VEGETARIAN, Health.PESCATARIAN, Health.EGG_FREE, Health.PEANUT_FREE,
                Health.TREE_NUT_FREE, Health.SOY_FREE, Health.FISH_FREE, Health.SHELLFISH_FREE,
                Health.PORK_FREE, Health.RED_MEAT_FREE, Health.CRUSTACEAN_FREE, Health.CELERY_FREE,
                Health.MUSTARD_FREE, Health.SESAME_FREE, Health.LUPINE_FREE, Health.MOLLUSK_FREE,
                Health.ALCOHOL_FREE, Health.KOSHER
            ),
            1253.2866475,
            List.of(CuisineType.KOSHER),
            null,
            List.of(DishType.STARTER),
            Arrays.asList(
                "4 to 6 sheets of matzo (depending on how thick you like the chocolate layer)",
                "1 1/2 sticks butter",
                "3/4 cups sugar",
                "14 ounces kosher for Passover semi-sweet chocolate",
                "14 ounces kosher for Passover milk chocolate"
            )
        );

        assertEquals(recipe, recipes.getFirst(),
            "It was expected to be the same recipe");
    }

    @Test
    void testSearchRecipesWithWrongIDAndKey() {
        RecipeRequest recipeRequest = RecipeRequest.builder("WRONG_ID", "WRONG_KEY").setKeyWords("chocolate").setHealthLabels(
            Health.EGG_FREE).setMealTypes(MealType.BREAKFAST).build();

        assertThrows(UnauthorizedAccessException.class, () -> cookingCompass.searchRecipes(recipeRequest,2),
            "It was expected UnauthorizedAccessException to be thrown");
    }

    @Test
    void testSearchRecipesWithZeroPages() throws Exception{
        RecipeRequest recipeRequest = RecipeRequest.builder("YOUR_ID", "YOUR_KEY").setKeyWords("chocolate").setHealthLabels(
            Health.EGG_FREE).setMealTypes(MealType.BREAKFAST).build();

        List<Recipe> recipes = cookingCompass.searchRecipes(recipeRequest,0);

        assertTrue(recipes.isEmpty(),
            "It was expected the list to be empty when 0 pages are read");
    }
}
