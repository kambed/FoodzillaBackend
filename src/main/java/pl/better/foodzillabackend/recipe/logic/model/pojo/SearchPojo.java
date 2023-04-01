package pl.better.foodzillabackend.recipe.logic.model.pojo;

import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSortPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;

public record SearchPojo(
        String phrase,
        Integer currentPage,
        Integer pageSize,
        RecipeSortPojo sort,
        SortDirectionPojo direction,
        RecipeFilterPojo filter
) {
}

