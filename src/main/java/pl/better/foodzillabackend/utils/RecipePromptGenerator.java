package pl.better.foodzillabackend.utils;

import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.stream.Collectors;

public class RecipePromptGenerator {

    private RecipePromptGenerator() {
    }

    public static String generatePrompt(RecipeDto recipe) {
        String ingredients = recipe
                .getIngredients()
                .stream()
                .map(IngredientDto::name)
                .collect(Collectors.joining(","));
        return String.format("%s made from: %s", recipe.getName(), ingredients);
    }
}
