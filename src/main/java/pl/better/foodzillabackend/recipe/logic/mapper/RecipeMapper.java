package pl.better.foodzillabackend.recipe.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RecipeMapper implements Function<RecipeDto, Recipe> {
    @Override
    public Recipe apply(RecipeDto recipe) {
        return Recipe.builder()
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
                .build();
    }
}
