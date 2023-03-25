package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.recipe.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.recipe.logic.service.IngredientService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;
    @QueryMapping
    public Set<IngredientDto> ingredients() {
        return ingredientService.getAllIngredients();
    }
}
