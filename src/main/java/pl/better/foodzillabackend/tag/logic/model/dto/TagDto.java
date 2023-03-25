package pl.better.foodzillabackend.tag.logic.model.dto;

import lombok.Builder;

@Builder
public record TagDto(Long id, String name) {
}