package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;

@Builder
public record IngredientDto(Long id, String name) {
}
