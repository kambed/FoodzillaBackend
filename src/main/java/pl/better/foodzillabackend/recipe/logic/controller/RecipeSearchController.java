package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SavedSearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.service.RecipeSearchService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class RecipeSearchController {

    private final RecipeSearchService recipeSearchService;

    @QueryMapping
    public SearchResultDto search(@Argument SearchPojo input) {
        return recipeSearchService.search(input);
    }

    @QueryMapping
    public List<SearchPojo> getSavedSearch() {
        return recipeSearchService.getSearches();
    }

    @SchemaMapping(typeName = "SearchResult", field = "opinion")
    public String opinion(SearchResultDto searchResult) {
        return recipeSearchService.getOpinion(searchResult);
    }
}
