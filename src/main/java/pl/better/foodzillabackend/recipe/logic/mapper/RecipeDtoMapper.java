package pl.better.foodzillabackend.recipe.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.ingredient.logic.mapper.IngredientDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.review.logic.mapper.ReviewDtoMapper;
import pl.better.foodzillabackend.tag.logic.mapper.TagDtoMapper;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecipeDtoMapper implements Function<Recipe, RecipeDto> {
    private final IngredientDtoMapper ingredientDtoMapper;
    private final TagDtoMapper tagDtoMapper;
    private final ReviewDtoMapper reviewDtoMapper;
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
                .reviews(recipe.getReviews().stream().map(reviewDtoMapper).collect(Collectors.toSet()))
                .ingredients(recipe.getIngredients().stream().map(ingredientDtoMapper).collect(Collectors.toSet()))
                .tags(recipe.getTags().stream().map(tagDtoMapper).collect(Collectors.toSet()))
                .build();
    }
}
