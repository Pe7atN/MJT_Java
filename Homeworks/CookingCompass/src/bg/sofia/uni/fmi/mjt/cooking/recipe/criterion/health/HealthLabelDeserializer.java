package bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class HealthLabelDeserializer implements JsonDeserializer<Health> {

    @Override
    public Health deserialize(JsonElement jsonElement,
                              Type type,
                              JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
        return Health
            .valueOf(
                jsonElement.getAsString().toUpperCase().replaceAll("[ -]", "_")
            );
    }
}
