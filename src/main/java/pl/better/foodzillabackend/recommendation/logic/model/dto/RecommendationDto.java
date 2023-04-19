package pl.better.foodzillabackend.recommendation.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.List;

@Builder
public record RecommendationDto(
        List<RecipeDto> recipes
) {
}
