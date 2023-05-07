package pl.better.foodzillabackend.search.logic.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping
public record CreateSearchCommand(
        @NotNull
        @Size(min = 1, max = 250,  message = "Search phrase must be between 1 and 250 characters.")
        String phrase,
        String filterAttribute,
        String filterEquals,
        String sortAttribute,
        Boolean isSortAscending
) {
}