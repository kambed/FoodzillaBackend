package pl.better.foodzillabackend.recipe.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import pl.better.foodzillabackend.utils.rabbitmq.Priority;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @QueryMapping
    public RecipeDto recipe(@Argument long id) {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return recipeService.getRecipeById(id, principal);
    }

    @SchemaMapping(typeName = "Recipe", field = "image")
    public String recipeImage(RecipeDto recipe) {
        return recipeService.getRecipeImageById(recipe, Priority.HIGH);
    }

    @SchemaMapping(typeName = "RecipeSummarization", field = "image")
    public String recipeSummarizationImage(RecipeDto recipe) {
        return recipeService.getRecipeImageById(recipe, Priority.MEDIUM);
    }

    @LoggedInUser
    @MutationMapping
    public RecipeDto createRecipe(@Argument @Valid CreateRecipeCommand recipe) {
        return recipeService.createNewRecipeAndSaveInDb(recipe);
    }
}
