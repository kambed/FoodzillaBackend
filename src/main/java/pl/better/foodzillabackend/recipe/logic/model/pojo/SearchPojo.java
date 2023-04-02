package pl.better.foodzillabackend.recipe.logic.model.pojo;

import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSort;

import java.util.Set;

public record SearchPojo(
        String phrase,
        Integer currentPage,
        Integer pageSize,
        Set<RecipeSort> sort,
        Set<RecipeFilterPojo> filters
) {
}

