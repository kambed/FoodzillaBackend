package pl.better.foodzillabackend.review.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT re FROM Recipe r LEFT JOIN r.reviews re WHERE r.id = :id")
    List<Review> findAllByRecipeId(long id);
}
