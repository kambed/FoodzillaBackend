package pl.better.foodzillabackend.recommendation.logic.model.dto;

import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

public record RecommendationDto(
        RecipeDto recipe,
        double preference
) {
}
