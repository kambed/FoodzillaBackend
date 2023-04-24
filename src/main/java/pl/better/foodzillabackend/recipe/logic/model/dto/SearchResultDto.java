package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchResultDto(
        Integer currentPage,
        Integer numberOfPages,
        List<RecipeDto> recipes
) {
}
