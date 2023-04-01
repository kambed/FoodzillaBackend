package pl.better.foodzillabackend.recipe.logic.model.dto;

import java.util.Set;

public record SearchResultDto(
        Integer currentPage,
        Integer numberOfPages,
        Set<RecipeDto> recipes
) {
}
