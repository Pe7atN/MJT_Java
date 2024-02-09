package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MealTypeDeserializer implements JsonDeserializer<MealType> {

    @Override
    public MealType deserialize(JsonElement jsonElement,
                                Type type,
                                JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
        return MealType.valueOf(
            jsonElement.getAsString().toUpperCase().replaceAll("/", "_")
        );
    }
}
