package pl.better.foodzillabackend.recommendation.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @QueryMapping
    @LoggedInUser
    public RecommendationDto recommendations() {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return recommendationService.recommendationsFromCustomer(principal);
    }

    @SchemaMapping(typeName = "Recommendations", field = "opinion")
    public String opinion(RecommendationDto recommendationDto) {
        return recommendationService.getOpinion(recommendationDto.recipes());
    }
}
