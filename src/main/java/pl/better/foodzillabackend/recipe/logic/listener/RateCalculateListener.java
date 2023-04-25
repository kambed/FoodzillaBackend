package pl.better.foodzillabackend.recipe.logic.listener;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
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
