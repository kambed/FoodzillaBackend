package pl.better.foodzillabackend.search.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Set<SearchDto> getSearches() {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Customer customer = getCustomer(principal);
        Set<Search> searches = searchRepository.getSearchByUserId(customer.getId()).orElseThrow(() -> new RecipeNotFoundException(
                SEARCH_NOT_FOUND.formatted(customer.getId())
        ));
        Set<SearchDto> mappedSearches = new HashSet<>();
        searches.forEach(search -> mappedSearches.add(searchDtoMapper.apply(search)));
        return mappedSearches;
    }

    @Transactional
    public SearchDto createNewSearchAndSaveInDb(CreateSearchCommand command) {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Customer customer = getCustomer(principal);

        Search search = Search.builder()
                .phrase(command.phrase())
                .filters(command.filters())
                .sort(command.sort())
                .build();

        customer.getSavedSearches().add(search);
        searchRepository.saveAndFlush(search);
        return searchDtoMapper.apply(search);
    }

    @Transactional
    public SearchDto deleteSavedSearch(String principal, long searchId) {
        Customer customer = getCustomer(principal);
        Search search = getSearch(searchId);

        searchRepository.delete(search);
        customer.getSavedSearches().remove(search);
        customerRepository.saveAndFlush(customer);

        return searchDtoMapper.apply(search);
    }

    private Customer getCustomer(String customer) {
        return customerRepository.findByUsername(customer)
                .orElseThrow(() -> {
                    throw new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            customer));
                });
    }

    private Search getSearch(long id) {
        return searchRepository.findById(id).orElseThrow(() -> {
            throw new RecipeNotFoundException(String.format(SEARCH_NOT_FOUND,
                    id));
        });
    }
}