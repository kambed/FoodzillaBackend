package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.service.FavouriteRecipeService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class FavouriteRecipeController {

    private final FavouriteRecipeService service;

    @LoggedInUser
    @QueryMapping
    public Set<RecipeDto> favouriteRecipes() {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.favouriteRecipes(principal);
    }

    @LoggedInUser
    @MutationMapping
    public Set<RecipeDto> addRecipeToFavourites(@Argument int recipeId) {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.addRecipeToFavourites(principal, recipeId);
    }

    @LoggedInUser
    @MutationMapping
    public Set<RecipeDto> removeRecipeFromFavourites(@Argument int recipeId) {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.removeRecipeFromFavourites(principal, recipeId);
    }
}
