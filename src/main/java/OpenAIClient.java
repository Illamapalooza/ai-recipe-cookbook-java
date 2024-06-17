package src.main.java;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenAIClient {
    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String API_KEY = dotenv.get("OPENAI_API_KEY");

    public String generateRecipe(String dishName) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");

        String inputJson = "{\"model\": \"gpt-3.5-turbo-instruct\", \"prompt\": \"This is a recipe for " + dishName + ":\", \"max_tokens\": 200}";

        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = inputJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (InputStream is = connection.getInputStream(); Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            String response = scanner.useDelimiter("\\A").next();
            System.out.println("Raw API Response: " + response); // Log raw API response
            return parseRecipeFromResponse(response);
        }
    }

    private String parseRecipeFromResponse(String response) {
        String textKey = "\"text\":";
        int startIndex = response.indexOf(textKey) + textKey.length();
        int endIndex = response.indexOf("\",", startIndex);
        if (endIndex == -1) {
            endIndex = response.length() - 1;
        }
        String recipeText = response.substring(startIndex, endIndex).replace("\\n", "\n").replace("\\\"", "\"");
        return recipeText.trim();
    }
}
