package pl.better.foodzillabackend.recipe.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.dto.IngredientDto;

import java.util.function.Function;

@Component
public class IngredientDtoMapper implements Function<Ingredient, IngredientDto> {
    @Override
    public IngredientDto apply(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }
}
