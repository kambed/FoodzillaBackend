package pl.better.foodzillabackend.search.logic.model.domain;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping
public record SearchFilters(
        String attribute,
        String equals
) {
}
