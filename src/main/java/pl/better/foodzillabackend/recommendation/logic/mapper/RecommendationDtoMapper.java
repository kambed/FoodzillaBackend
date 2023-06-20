package pl.better.foodzillabackend.recommendation.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;

import java.util.List;
import java.util.function.Function;

@Component
public class RecommendationDtoMapper implements Function<List<RecipeDto>, RecommendationDto> {
    @Override
    public RecommendationDto apply(List<RecipeDto> recipeDtos) {
        return RecommendationDto.builder()
                .recipes(recipeDtos)
                .build();
    }
}
