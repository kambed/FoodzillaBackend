package pl.better.foodzillabackend.recommendation.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.PythonErrorException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;

import pl.better.foodzillabackend.utils.retrofit.PythonApiClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final Environment environment;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";

    @Transactional
    public List<RecipeDto> recommend(String principal, int numOfRecommendations) {
        Customer customer = customerRepository
                .findByUsername(principal)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CUSTOMER_NOT_FOUND.formatted(principal)
                ));
        try {
            List<Long> recommendationIds = PythonApiClient
                    .create(environment.getProperty("BASE_PYTHON_URL"))
                    .predict(customer.getId(), numOfRecommendations)
                    .execute()
                    .body();
            customer.setRecommendations(recommendationIds);
            customerRepository.saveAndFlush(customer);
            return recipeRepository.getRecipesIds(recommendationIds)
                    .stream()
                    .map(recipeDtoMapper)
                    .toList();
        } catch (Exception e) {
            throw new PythonErrorException("Error during using python module");
        }
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> recommendationsFromCustomer(String principal) {
        Customer customer = customerRepository
                .findByUsername(principal)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CUSTOMER_NOT_FOUND.formatted(principal)
                ));
        List<Long> recommendationIds = customer.getRecommendations();
        return recipeRepository
                .getRecipesIds(recommendationIds)
                .stream()
                .map(recipeDtoMapper)
                .toList();
    }
}
