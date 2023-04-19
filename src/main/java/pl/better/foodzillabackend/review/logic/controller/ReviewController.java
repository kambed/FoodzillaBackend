package pl.better.foodzillabackend.review.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;

import pl.better.foodzillabackend.review.logic.model.command.CreateReviewCommand;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;
import pl.better.foodzillabackend.review.logic.service.ReviewService;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @LoggedInUser
    @MutationMapping
    public ReviewDto createReview(@Argument @Valid CreateReviewCommand command) {
        ReviewDto reviewDto = reviewService.createReview(command);
        reviewService.train();
        return reviewDto;
    }
}
