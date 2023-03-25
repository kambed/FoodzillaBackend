package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;

@Builder
public record TagDto(Long id, String name) {
}