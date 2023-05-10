package pl.better.foodzillabackend.search.logic.model.domain;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.io.Serializable;

@SchemaMapping
public record SearchSort(
        String attribute,
        String direction
) implements Serializable {
}
