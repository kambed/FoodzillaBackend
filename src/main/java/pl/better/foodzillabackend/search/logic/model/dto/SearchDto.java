package pl.better.foodzillabackend.search.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.search.logic.model.domain.SearchFilters;
import pl.better.foodzillabackend.search.logic.model.domain.SearchSort;

import java.util.Set;

@Builder
public record SearchDto(
        Long id,
        String phrase,
        Set<SearchFilters> filters,
        Set<SearchSort> sort
) {
}