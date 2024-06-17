package src.main.java;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecipeManager {
    private Map<String, String> recipes;
    private OpenAIClient openAIClient;

    public RecipeManager() {
        recipes = new HashMap<>();
        openAIClient = new OpenAIClient();
    }

    public String generateRecipe(String dishName) throws IOException {
        return openAIClient.generateRecipe(dishName);
    }

    public void saveRecipe(String dishName, String recipe) {
        recipes.put(dishName, recipe);
    }

    public String getRecipe(String dishName) {
        return recipes.get(dishName);
    }

    public void deleteRecipe(String dishName) {
        recipes.remove(dishName);
    }
}
