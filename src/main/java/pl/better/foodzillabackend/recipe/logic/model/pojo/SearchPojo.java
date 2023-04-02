package pl.better.foodzillabackend.recipe.logic.model.pojo;

import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSortPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;

import java.util.Set;

public record SearchPojo(
        String phrase,
        Integer currentPage,
        Integer pageSize,
        RecipeSortPojo sort,
        SortDirectionPojo direction,
        Set<RecipeFilterPojo> filters
) {
}

