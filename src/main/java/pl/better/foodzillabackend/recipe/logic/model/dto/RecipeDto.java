package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;

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
        double rating,
        String image,
        Set<ReviewDto> reviews,
        Set<IngredientDto> ingredients,
        Set<TagDto> tags
) {
}
