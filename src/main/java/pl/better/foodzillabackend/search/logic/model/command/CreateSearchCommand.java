package pl.better.foodzillabackend.search.logic.model.command;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSort;

import java.util.Set;


@SchemaMapping
public record CreateSearchCommand(
        String phrase,
        Set<RecipeFilterPojo> filters,
        Set<RecipeSort> sort
) {
}
