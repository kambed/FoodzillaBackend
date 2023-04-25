package pl.better.foodzillabackend.recipe.logic.listener;

import jakarta.persistence.PostUpdate;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

public class RateCalculateListener {

    @PostUpdate
    public void calculateAvgOpinion(Recipe recipe) {
        double result = recipe.getReviews()
                .stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        recipe.setRating(Math.round(result * 100.0) / 100.0);
    }
}
