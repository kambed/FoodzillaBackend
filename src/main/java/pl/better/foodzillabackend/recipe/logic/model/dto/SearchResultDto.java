package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record SearchResultDto(
        Integer currentPage,
        Integer numberOfPages,
        Set<RecipeDto> recipes
) {
}
