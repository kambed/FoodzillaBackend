package pl.better.foodzillabackend.search.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.search.logic.model.domain.Search;
import pl.better.foodzillabackend.search.logic.model.domain.SearchFilters;
import pl.better.foodzillabackend.search.logic.model.domain.SearchSort;
import pl.better.foodzillabackend.search.logic.model.dto.SearchDto;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class SearchDtoMapper implements Function<Search, SearchDto> {
    @Override
    public SearchDto apply(Search search) {

        Set<SearchFilters> filters = new HashSet<>();
        SearchFilters sf = new SearchFilters(search.getFilterAttribute(), search.getFilterEquals());
        filters.add(sf);

        Set<SearchSort> sort = new HashSet<>();
        SearchSort ss = new SearchSort(search.getSortAttribute(), search.getSortDirection());
        sort.add(ss);

        return SearchDto.builder()
                .id(search.getId())
                .phrase(search.getPhrase())
                .filters(filters)
                .sort(sort)
                .build();
    }
}