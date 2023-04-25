package pl.better.foodzillabackend.recipe.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.function.Function;

@Component
public class RecipeDtoMapper implements Function<Recipe, RecipeDto> {
    @Override
    public RecipeDto apply(Recipe recipe) {
        return RecipeDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .timeOfPreparation(recipe.getTimeOfPreparation())
                .numberOfSteps(recipe.getNumberOfSteps())
                .steps(recipe.getSteps())
                .numberOfIngredients(recipe.getNumberOfIngredients())
                .calories(recipe.getCalories())
                .fat(recipe.getFat())
                .sugar(recipe.getSugar())
                .sodium(recipe.getSodium())
                .protein(recipe.getProtein())
                .saturatedFat(recipe.getSaturatedFat())
                .carbohydrates(recipe.getCarbohydrates())
                .rating(recipe.getRating())
                .image(recipe.getImage())
                .reviews(recipe.getReviews())
                .ingredients(recipe.getIngredients())
                .tags(recipe.getTags())
                .build();
    }
}
