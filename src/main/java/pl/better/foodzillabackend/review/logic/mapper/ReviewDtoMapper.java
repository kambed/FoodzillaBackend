package pl.better.foodzillabackend.review.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.review.logic.model.domain.Review;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;

import java.util.function.Function;

@Component
public class ReviewDtoMapper implements Function<Review, ReviewDto> {
    @Override
    public ReviewDto apply(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .review(review.getReview())
                .rating(review.getRating())
                .build();
    }
}
