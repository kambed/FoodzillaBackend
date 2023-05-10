package pl.better.foodzillabackend.search.logic.model.domain;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.io.Serializable;

@SchemaMapping
public record SearchFilters(
        String attribute,
        String equals,
        int from,
        int to,
        String[] in,
        String[] hasOnly
) implements Serializable { }