package pl.better.foodzillabackend.recommendation.logic.service;

import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;

import java.util.List;

@Service
public class RecommendationService {

    public List<RecommendationDto> recommend(long id, int numOfRecommendations) {
        //TODO: CONNECT TO PYTHON MODULE
        return null;
    }
}
