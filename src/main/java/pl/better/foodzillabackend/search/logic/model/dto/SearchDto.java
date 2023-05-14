package pl.better.foodzillabackend.search.logic.model.dto;

import lombok.Builder;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSort;

import java.util.Set;

@Builder
public record SearchDto(
        Long id,
        String phrase,
        Set<RecipeFilterPojo> filters,
        Set<RecipeSort> sort
) {
}