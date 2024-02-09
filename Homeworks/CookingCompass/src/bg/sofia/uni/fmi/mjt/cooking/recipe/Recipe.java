package bg.sofia.uni.fmi.mjt.cooking.recipe;

import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.cuisine.CuisineType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet.Diet;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.dishtype.DishType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.Health;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealType;

import java.util.List;

public record Recipe(String label, List<Diet> dietLabels, List<Health> healthLabels, double totalWeight,
                     List<CuisineType> cuisineType, List<MealType> meatType, List<DishType> dishType,
                     List<String> ingredientLines) {
}

