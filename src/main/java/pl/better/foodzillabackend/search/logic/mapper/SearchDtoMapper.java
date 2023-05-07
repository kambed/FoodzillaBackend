package pl.better.foodzillabackend.search.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.search.logic.model.domain.Search;
import pl.better.foodzillabackend.search.logic.model.dto.SearchDto;

import java.util.function.Function;

@Component
public class SearchDtoMapper implements Function<Search, SearchDto> {
    @Override
    public SearchDto apply(Search search) {
        return SearchDto.builder()
                .id(search.getId())
                .phrase(search.getPhrase())
                .filterAttribute(search.getFilterAttribute())
                .filterEquals(search.getFilterEquals())
                .sortAttribute(search.getSortAttribute())
                .isSortAscending(search.getIsSortAscending())
                .build();
    }
}