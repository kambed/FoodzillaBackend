package pl.better.foodzillabackend.recommendation.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

@Builder
public record RecommendationDto(
        RecipeDto recipe,
        double preference
) {
}
