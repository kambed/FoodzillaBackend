package pl.better.foodzillabackend.recipe.logic.model.pojo.filter;

import java.util.Set;

public record RecipeFilterPojo(
        Set<FilterIntTypePojo> timeOfPreparation,
        Set<FilterStringTypePojo> tags,
        Set<FilterStringTypePojo> ingredients,
        Set<FilterIntTypePojo> numberOfSteps,
        Set<FilterIntTypePojo> numberOfIngredients,
        Set<FilterIntTypePojo> calories,
        Set<FilterIntTypePojo> totalFat,
        Set<FilterIntTypePojo> sugar,
        Set<FilterIntTypePojo> sodium,
        Set<FilterIntTypePojo> protein,
        Set<FilterIntTypePojo> saturatedFat,
        Set<FilterIntTypePojo> carbohydrates,
        Set<FilterIntTypePojo> cholesterol
) {
}
