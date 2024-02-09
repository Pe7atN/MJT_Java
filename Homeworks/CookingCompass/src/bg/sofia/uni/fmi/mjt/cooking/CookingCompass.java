package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.cooking.exception.UnauthorizedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.recipe.RecipeRequest;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.cuisine.CuisineType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.cuisine.CuisineTypeDeserializer;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet.Diet;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.diet.DietLabelDeserializer;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.dishtype.DishType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.dishtype.DishTypeDeserializer;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.Health;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.health.HealthLabelDeserializer;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealType;
import bg.sofia.uni.fmi.mjt.cooking.recipe.criterion.mealtype.MealTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CookingCompass implements CookingCompassAPI {
    private final Gson gson;
    private HttpClient client;

    private static final String RECIPE_INDEX = "recipe";
    private static final String RECIPE_LIST_INDEX = "hits";
    private static final String LINKS_INDEX = "_links";
    private static final String NEXT_PAGE_INDEX = "next";
    private static final String HREF_INDEX = "href";


    private static final int ERROR_CODE_UNAUTHORIZED_ACCESS = 401;
    private static final int ERROR_CODE_FOR_SERVER_ERROR_FROM = 500;
    private static final int ERROR_CODE_FOR_SERVER_ERROR_TO = 505;

    public CookingCompass() {
        gson = new GsonBuilder()
            .registerTypeAdapter(CuisineType.class, new CuisineTypeDeserializer())
            .registerTypeAdapter(DishType.class, new DishTypeDeserializer())
            .registerTypeAdapter(Health.class, new HealthLabelDeserializer())
            .registerTypeAdapter(MealType.class, new MealTypeDeserializer())
            .registerTypeAdapter(Diet.class, new DietLabelDeserializer())
            .create();
        this.client = HttpClient.newHttpClient();
    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

    public List<Recipe> searchRecipes(RecipeRequest request, int pages) throws ServerErrorException,
        UnauthorizedAccessException {

        if (pages == 0) {
            return List.of();
        }

        String requestString = request.createRequestString();
        HttpResponse<String> response = null;

        try {
            response = sendRequest(requestString);
        } catch (IOException e) {
            throw new UncheckedIOException("There was an I/O problem ", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Action was interrupted", e);
        }

        return getRecipesFromResponse(response, pages);
    }

    private List<Recipe> getRecipesFromResponse(HttpResponse<String> response, int pages)
        throws UnauthorizedAccessException, ServerErrorException {
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray hits = jsonObject.getAsJsonArray(RECIPE_LIST_INDEX);
        List<Recipe> recipes = new ArrayList<>();
        for (JsonElement el : hits) {
            Recipe r = gson.fromJson(el.getAsJsonObject().get(RECIPE_INDEX), Recipe.class);
            recipes.add(r);
        }

        JsonObject links = jsonObject.getAsJsonObject(LINKS_INDEX);
        if (pages > 1 && links != null && links.has(NEXT_PAGE_INDEX)) {
            String nextPage = links.getAsJsonObject(NEXT_PAGE_INDEX).get(HREF_INDEX).toString().replace("\"", "");
            HttpResponse<String> responseNext = null;
            try {
                responseNext = sendRequest(nextPage);
            } catch (IOException e) {
                throw new UncheckedIOException("There was an I/O problem", e);
            } catch (InterruptedException e) {
                throw new RuntimeException("Action was interrupted", e);
            }

            recipes.addAll(getRecipesFromResponse(responseNext, pages - 1));
        }

        return recipes;
    }

    private HttpResponse<String> sendRequest(String request)
        throws IOException, InterruptedException, UnauthorizedAccessException, ServerErrorException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(request)).build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == ERROR_CODE_UNAUTHORIZED_ACCESS) {
            throw new UnauthorizedAccessException("The ApplicationID and ApplicationKey were not valid");
        } else if (response.statusCode() >= ERROR_CODE_FOR_SERVER_ERROR_FROM &&
            response.statusCode() <= ERROR_CODE_FOR_SERVER_ERROR_TO) {
            throw new ServerErrorException("There was a problem with the server");
        }

        return response;
    }
}
