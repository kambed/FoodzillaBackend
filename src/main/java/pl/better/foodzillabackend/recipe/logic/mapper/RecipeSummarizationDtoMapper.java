package pl.better.foodzillabackend.recipe.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.function.Function;

@Component
public class RecipeSummarizationDtoMapper implements Function<Recipe, RecipeDto> {

    @Override
    public RecipeDto apply(Recipe recipe) {
        return RecipeDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .timeOfPreparation(recipe.getTimeOfPreparation())
                .image(recipe.getImage())
                .build();
    }
}
