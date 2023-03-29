package pl.better.foodzillabackend.ingredient.logic.model.dto;

import lombok.Builder;

@Builder
public record IngredientDto(Long id, String name) {
}
