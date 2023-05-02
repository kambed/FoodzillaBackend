package pl.better.foodzillabackend.utils;

import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.stream.Collectors;

public class RecipePromptGenerator {

    private RecipePromptGenerator() {
    }

    public static String generatePrompt(Recipe recipe) {
        String ingredients = recipe
                .getIngredients()
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.joining(","));
        return String.format("%s made from: %s", recipe.getName(), ingredients);
    }
}
