package pl.better.foodzillabackend.search.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.search.logic.mapper.SearchDtoMapper;
import pl.better.foodzillabackend.search.logic.model.command.CreateSearchCommand;
import pl.better.foodzillabackend.search.logic.model.domain.Search;
import pl.better.foodzillabackend.search.logic.model.dto.SearchDto;
import pl.better.foodzillabackend.search.logic.repository.SearchRepository;
import pl.better.foodzillabackend.utils.rabbitmq.PublisherMq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final String SEARCH_NOT_FOUND = "Search with given id %s not found";
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private final SearchRepository searchRepository;
    private final SearchDtoMapper searchDtoMapper;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PublisherMq publisherMq;

    @Transactional
    public SearchDto getSearchById(long id) {
        Search search = searchRepository.getSearchById(id).orElseThrow(() -> new RecipeNotFoundException(
                SEARCH_NOT_FOUND.formatted(id)
        ));
        return searchDtoMapper.apply(search);
    }

    public <T> Set<T> getSearchItems(List<T> items) {
        if (items.size() == 1 && items.get(0) == null) {
            items = List.of();
        }
        return new HashSet<>(items);
    }

    @Transactional
    public SearchDto createNewSearchAndSaveInDb(CreateSearchCommand command) {
        Search search = Search.builder()
                .phrase(command.phrase())
                .filterAttribute(command.filterAttribute())
                .filterEquals(command.filterEquals())
                .sortAttribute(command.sortAttribute())
                .isSortAscending(command.isSortAscending())
                .build();
        searchRepository.saveAndFlush(search);
        return searchDtoMapper.apply(search);
    }

//    @Transactional
//    public Set<SearchDto> deleteSavedSearch(String principal, long searchId) {
//        Customer customer = getCustomer(principal);
//
//        customer.getFavouriteRecipes().remove(recipe);
//        customerRepository.saveAndFlush(customer);
//
//        return customer.getFavouriteRecipes()
//                .stream()
//                .map(recipeDtoMapper)
//                .collect(Collectors.toSet());
//    }

    private Customer getCustomer(String customer) {
        return customerRepository.findByUsername(customer)
                .orElseThrow(() -> {
                    throw new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            customer));
                });
    }
}