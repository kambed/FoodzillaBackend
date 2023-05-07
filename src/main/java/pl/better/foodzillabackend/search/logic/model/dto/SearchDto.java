package pl.better.foodzillabackend.search.logic.model.dto;

import lombok.Builder;

@Builder
public record SearchDto(
        Long id,
        String phrase,
        String filterAttribute,
        String filterEquals,
        String sortAttribute,
        Boolean isSortAscending
) {
}