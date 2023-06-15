package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.logic.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.service.RecentlyViewedRecipesService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecentlyViewedRecipesController {

    private final RecentlyViewedRecipesService service;

    @LoggedInUser
    @QueryMapping
    public List<RecipeDto> recentlyViewedRecipes() {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return service.getRecentlyViewedRecipes(principal);
    }
}
