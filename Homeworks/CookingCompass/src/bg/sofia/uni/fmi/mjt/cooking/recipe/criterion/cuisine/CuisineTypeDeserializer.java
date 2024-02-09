package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.cuisine;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CuisineTypeDeserializer implements JsonDeserializer<CuisineType> {

    @Override
    public CuisineType deserialize(JsonElement jsonElement,
                                Type type,
                                JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
        return CuisineType.valueOf(
            jsonElement.getAsString().toUpperCase().replaceAll(" ", "_")
        );
    }

}
