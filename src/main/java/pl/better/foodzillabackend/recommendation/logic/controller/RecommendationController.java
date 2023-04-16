package pl.better.foodzillabackend.recommendation.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @QueryMapping
    @LoggedInUser
    public List<RecommendationDto> recommendations(@Argument long id, @Argument int numOfRecommendations) { //TODO: Replace ID with id from Bearer
        return recommendationService.recommend(id, numOfRecommendations);
    }
}
