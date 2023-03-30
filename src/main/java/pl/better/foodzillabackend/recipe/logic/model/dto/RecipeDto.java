package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.List;
import java.util.Set;

@Builder
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
        String image,
        Set<Review> reviews,
        Set<Ingredient> ingredients,
        Set<Tag> tags
) {
}
