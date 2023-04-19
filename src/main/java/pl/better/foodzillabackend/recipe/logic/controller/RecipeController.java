package pl.better.foodzillabackend.recipe.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
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

    @SchemaMapping(typeName = "Recipe", field = "image")
    public String recipeImage(RecipeDto recipe) {
        return recipeService.getRecipeImageById(recipe);
    }

    @LoggedInUser
    @MutationMapping
    public RecipeDto createRecipe(@Argument @Valid CreateRecipeCommand recipe) {
        return recipeService.createNewRecipeAndSaveInDb(recipe);
    }
}
