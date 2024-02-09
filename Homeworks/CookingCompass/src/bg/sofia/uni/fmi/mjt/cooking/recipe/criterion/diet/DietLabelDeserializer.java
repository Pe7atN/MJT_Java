package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DietLabelDeserializer implements JsonDeserializer<Diet> {

    @Override
    public Diet deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
        return Diet.valueOf(
            jsonElement.getAsString().toUpperCase().replaceAll("-", "_")
        );
    }
}
