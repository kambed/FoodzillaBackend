package pl.better.foodzillabackend.ingredient.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;

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
