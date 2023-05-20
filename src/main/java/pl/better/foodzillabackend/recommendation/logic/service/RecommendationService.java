package pl.better.foodzillabackend.recommendation.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.RecommendationErrorException;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.recommendation.logic.mapper.RecommendationDtoMapper;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;
import pl.better.foodzillabackend.utils.rabbitmq.recipeimage.ImagePublisher;
import pl.better.foodzillabackend.utils.retrofit.completions.api.CompletionsAdapter;
import pl.better.foodzillabackend.utils.retrofit.recommendations.api.RecommendationAdapter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CustomerRepository customerRepository;
    private final RecipeRepositoryAdapter recipeRepository;
    private final RecommendationAdapter recommendationAdapter;
    private final ImagePublisher imagePublisher;
    private final CompletionsAdapter completionsAdapter;
    private final RecommendationDtoMapper recommendationDtoMapper;
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
            recommendationIds.forEach(
                    id -> imagePublisher.send(Priority.IDLE, recipeRepository.getRecipeById(id))
            );
        } catch (Exception e) {
            throw new RecommendationErrorException("Error during using recommendation module");
        }
    }

    @Transactional(readOnly = true)
    public RecommendationDto recommendationsFromCustomer(String principal) {
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
        return recommendationDtoMapper.apply(recipeRepository.getRecipesByIds(recommendationIds));
    }

    @Async("recommendationTaskExecutor")
    public void train() {
        try {
            recommendationAdapter.train();
        } catch (Exception e) {
            throw new RecommendationErrorException("Error during using python module");
        }
    }


    public String getOpinion(List<RecipeDto> recipes) {
        return completionsAdapter.generateCompletion(
                "What do you think about this recipes: " + recipes.toString()
        );
    }
}
