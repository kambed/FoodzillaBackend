package pl.better.foodzillabackend.recipe.logic.model.pojo.filter;

import java.util.Set;

public record RecipeFilterPojo(
        String attribute,
        String equals,
        String from,
        String to,
        Set<String> in
) {
}
