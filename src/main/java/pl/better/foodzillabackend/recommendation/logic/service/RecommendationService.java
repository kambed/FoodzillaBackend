package pl.better.foodzillabackend.recommendation.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
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

    @Transactional(readOnly = true)
    public List<RecipeDto> recommend(String principal, int numOfRecommendations) {
        Long id = customerRepository.findByUsername(principal).orElseThrow().getId();
        train();
        try {
            List<Long> recommendationIds = PythonApiClient.create().predict(id, numOfRecommendations).execute().body();
            return recipeRepository.getRecipesIds(recommendationIds).stream().map(recipeDtoMapper).toList();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void train() {
        try {
            PythonApiClient.create().trainModel(10).execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
