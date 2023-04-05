package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
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
        return null;
    }

    @LoggedInUser
    @MutationMapping
    public Set<RecipeDto> addRecipeToFavourites() {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }

    @LoggedInUser
    @MutationMapping
    public Set<RecipeDto> removeRecipeFromFavourites() {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }
}
