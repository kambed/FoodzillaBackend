package pl.better.foodzillabackend.review.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
