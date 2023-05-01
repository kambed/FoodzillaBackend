package pl.better.foodzillabackend.recommendation.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.RecommendationErrorException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeSummarizationDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;
import pl.better.foodzillabackend.utils.rabbitmq.PublisherMq;
import pl.better.foodzillabackend.utils.retrofit.recommendations.api.RecommendationAdapter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeSummarizationDtoMapper recipeSummarizationDtoMapper;
    private final RecommendationAdapter recommendationAdapter;
    private final PublisherMq publisherMq;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";

    @Async("recommendationTaskExecutor")
    public void recommend(String principal) {
        Customer customer = customerRepository
                .findByUsername(principal)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CUSTOMER_NOT_FOUND.formatted(principal)
                ));
        try {
            List<Long> recommendationIds = recommendationAdapter.getRecommendations(customer.getId());
            customer.setRecommendations(recommendationIds);
            customerRepository.saveAndFlush(customer);
            recipeRepository.getRecipesSummarizationIds(recommendationIds)
                    .stream()
                    .filter(recipe -> recipe.getImage() == null)
                    .forEach(recipe -> publisherMq.sendAndReceive(
                                    Priority.IDLE.getPriorityValue(),
                                    recipe
                            ).thenAccept(
                                    rabbitMessage -> {
                                        recipe.setImage(new String(rabbitMessage.getBody()));
                                        recipeRepository.saveAndFlush(recipe);
                                    }
                            )
                    );
        } catch (Exception e) {
            throw new RecommendationErrorException("Error during using recommendation module");
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
        if (recommendationIds == null) {
            try {
                recommendationIds = recommendationAdapter.getRecommendations(customer.getId());
            } catch (Exception e) {
                throw new RecommendationErrorException("Error during using recommendations module");
            }
        }
        return recipeRepository
                .getRecipesSummarizationIds(recommendationIds)
                .stream()
                .map(recipeSummarizationDtoMapper)
                .toList();
    }

    @Async("recommendationTaskExecutor")
    public void train() {
        try {
            recommendationAdapter.train();
        } catch (Exception e) {
            throw new RecommendationErrorException("Error during using python module");
        }
    }
}
