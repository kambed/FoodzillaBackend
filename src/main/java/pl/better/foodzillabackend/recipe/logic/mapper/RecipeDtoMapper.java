package pl.better.foodzillabackend.recipe.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.function.Function;

@Component
public class RecipeDtoMapper implements Function<Recipe, RecipeDto> {
    @Override
    public RecipeDto apply(Recipe recipe) {
        return new RecipeDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getTimeOfPreparation(),
                recipe.getNumberOfSteps(),
                recipe.getSteps(),
                recipe.getNumberOfIngredients(),
                recipe.getCalories(),
                recipe.getFat(),
                recipe.getSugar(),
                recipe.getSodium(),
                recipe.getProtein(),
                recipe.getSaturatedFat(),
                recipe.getCarbohydrates(),
                recipe.getReviews(),
                recipe.getIngredients(),
                recipe.getTags()
        );
    }
}
