package pl.better.foodzillabackend.review.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.recipe.logic.listener.RateCalculateListener;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;
import pl.better.foodzillabackend.review.logic.mapper.ReviewDtoMapper;
import pl.better.foodzillabackend.review.logic.model.command.CreateReviewCommand;
import pl.better.foodzillabackend.review.logic.model.domain.Review;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final RecommendationService recommendationService;
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepositoryAdapter recipeRepository;
    private final ReviewDtoMapper reviewDtoMapper;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";

    @Transactional
    public ReviewDto createReview(CreateReviewCommand command) {
        RateCalculateListener rateCalculateListener = new RateCalculateListener();

        String principal = SecurityContextHolder
                .getContext()
                .getAuthentication().getName();

        Customer customer = customerRepository
                .findByUsername(principal)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CUSTOMER_NOT_FOUND.formatted(principal)
                ));

        Recipe recipe = recipeRepository.findById(command.recipeId());

        Review review = new Review(command.review(),
                command.rating(),
                customer,
                recipe);

        reviewRepository.saveAndFlush(review);
        recommendationService.train();
        recommendationService.recommend(principal);

        rateCalculateListener.calculateAvgOpinion(recipe);

        return reviewDtoMapper.apply(review);
    }
}
