package pl.better.foodzillabackend.search.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.search.logic.model.command.CreateSearchCommand;
import pl.better.foodzillabackend.search.logic.model.dto.SearchDto;
import pl.better.foodzillabackend.search.logic.service.SearchService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @QueryMapping
    public SearchDto savedSearch(@Argument long id) {
        return searchService.getSearchById(id);
    }

    @LoggedInUser
    @MutationMapping
    public SearchDto addSavedSearch(@Argument @Valid CreateSearchCommand search) {
        return searchService.createNewSearchAndSaveInDb(search);
    }

//    @LoggedInUser
//    @MutationMapping
//    public Set<SearchDto> deleteSavedSearch(@Argument long id) {
//        String principal = SecurityContextHolder.getContext()
//                .getAuthentication().getName();
//        return searchService.deleteSavedSearch(principal, id);
//    }
}
