package bg.sofia.uni.fmi.mjt.cooking;

import bg.sofia.uni.fmi.mjt.cooking.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.cooking.exception.UnauthorizedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.cooking.recipe.RecipeRequest;

import java.net.http.HttpClient;
import java.util.List;

public interface CookingCompassAPI {

    /**
     * Set the http client with a certain one
     *
     * @param client the http client which will send the request
     */
    void setClient(HttpClient client);

    /**
     * @return the http client which will send the request
     */
    HttpClient getClient();

    /**
     * Returns a list of recipes associated with the certain filters which are set with
     * the recipe request
     *
     * @param request the recipe request with the needed filters
     * @param pages   the pages that needs to be searched
     * @throws UnauthorizedAccessException If the application id or the application key is not a valid API registration
     * @throws ServerErrorException        If the connection to the server is not possible
     */
    List<Recipe> searchRecipes(RecipeRequest request, int pages) throws ServerErrorException,
        UnauthorizedAccessException;
}
