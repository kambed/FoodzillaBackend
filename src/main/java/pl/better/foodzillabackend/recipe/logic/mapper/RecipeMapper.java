package pl.better.foodzillabackend.recipe.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.function.Function;

@Component
public class RecipeMapper implements Function<RecipeDto, Recipe> {
    @Override
    public Recipe apply(RecipeDto recipeDto) {
        return Recipe.builder()
                .id(recipeDto.id())
                .name(recipeDto.name())
                .description(recipeDto.description())
                .timeOfPreparation(recipeDto.timeOfPreparation())
                .numberOfSteps(recipeDto.numberOfSteps())
                .steps(recipeDto.steps())
                .numberOfIngredients(recipeDto.numberOfIngredients())
                .calories(recipeDto.calories())
                .fat(recipeDto.fat())
                .sugar(recipeDto.sugar())
                .sodium(recipeDto.sodium())
                .protein(recipeDto.protein())
                .saturatedFat(recipeDto.saturatedFat())
                .carbohydrates(recipeDto.carbohydrates())
                .image(recipeDto.image())
                .reviews(recipeDto.reviews())
                .ingredients(recipeDto.ingredients())
                .tags(recipeDto.tags())
                .build();
    }
}
