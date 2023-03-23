package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.service.RecipeService;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @QueryMapping
    public RecipeDto recipe(@Argument long id) {
        return recipeService.getRecipeById(id);
    }

    @MutationMapping
    public RecipeDto createRecipe(@Argument CreateRecipeCommand recipe) {
        return recipeService.createNewRecipeAndSaveInDb(recipe);
    }

}
