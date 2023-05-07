package pl.better.foodzillabackend.recipe.logic.model.domain;

import lombok.Builder;

@Builder
public record RecipeShort(
        long id,
        String prompt
) {
}
