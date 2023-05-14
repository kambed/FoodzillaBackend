package pl.better.foodzillabackend.search.logic.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSort;

import java.util.Set;


@SchemaMapping
public record CreateSearchCommand(
        @NotNull
        @Size(min = 1, max = 250,  message = "Search phrase must be between 1 and 250 characters.")
        String phrase,
        Set<RecipeFilterPojo> filters,
        Set<RecipeSort> sort
) {
}
