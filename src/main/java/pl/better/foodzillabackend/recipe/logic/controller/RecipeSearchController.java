package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.service.RecipeSearchService;

@Controller
@RequiredArgsConstructor
public class RecipeSearchController {

    private final RecipeSearchService recipeSearchService;

    @QueryMapping
    public SearchResultDto search(@Argument SearchPojo input) {
        return recipeSearchService.search(input);
    }
}
