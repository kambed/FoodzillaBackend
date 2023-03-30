package pl.better.foodzillabackend.recommendation.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.service.RecipeService;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RecommendationDtoMapper implements Function<RecommendedItem, RecommendationDto> {

    private final RecipeService recipeService;

    @Override
    public RecommendationDto apply(RecommendedItem recommendedItem) {
        return RecommendationDto.builder()
                .recipe(recipeService.getRecipeById(recommendedItem.getItemID()))
                .preference(recommendedItem.getValue()).build();
    }
}
