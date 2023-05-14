package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeSummarizationDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentlyViewedRecipesService {
    private final CustomerRepository customerRepository;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private final RecipeSummarizationDtoMapper recipeSummarizationDtoMapperMapper;

    @Transactional
    public Set<RecipeDto> getRecentlyViewedRecipes(String principal) {

        Customer customer = customerRepository.findByUsername(principal)
                .orElseThrow(() -> {
                    throw new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            principal));
                });

        return customer
                .getRecentlyViewedRecipes()
                .stream()
                .map(recipeSummarizationDtoMapperMapper)
                .collect(Collectors.toSet());
    }
}
