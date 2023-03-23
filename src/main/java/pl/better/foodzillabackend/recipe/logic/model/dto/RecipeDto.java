package pl.better.foodzillabackend.recipe.logic.model.dto;

import pl.better.foodzillabackend.recipe.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Tag;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.List;
import java.util.Set;

public record RecipeDto(
        Long id,
        String name,
        String description,
        int timeOfPreparation,
        int numberOfSteps,
        List<String> steps,
        int numberOfIngredients,
        int calories,
        int fat,
        int sugar,
        int sodium,
        int protein,
        int saturatedFat,
        int carbohydrates,
        Set<Review> reviews,
        Set<Ingredient> ingredients,
        Set<Tag> tags
) {
}
