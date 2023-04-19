package pl.better.foodzillabackend.review.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.PythonErrorException;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.review.logic.mapper.ReviewDtoMapper;
import pl.better.foodzillabackend.review.logic.model.command.CreateReviewCommand;
import pl.better.foodzillabackend.review.logic.model.domain.Review;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;
import pl.better.foodzillabackend.utils.retrofit.PythonApiClient;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final Environment environment;
    private final ReviewDtoMapper reviewDtoMapper;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";

    @Transactional
    public ReviewDto createReview(CreateReviewCommand command) {
        String principal = SecurityContextHolder
                .getContext()
                .getAuthentication().getName();

        Customer customer = customerRepository
                .findByUsername(principal)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CUSTOMER_NOT_FOUND.formatted(principal)
                ));

        Recipe recipe = recipeRepository
                .findById(command.recipeId())
                .orElseThrow(() -> new RecipeNotFoundException(
                        RECIPE_NOT_FOUND_MESSAGE.formatted(command.recipeId())
                ));

        Review review = new Review(command.review(),
                command.rating(),
                customer,
                recipe);

        reviewRepository.saveAndFlush(review);

        return reviewDtoMapper.apply(review);
    }

    @Async("recommendationTaskExecutor")
    public void train() {
        try {
            PythonApiClient.create(environment
                            .getProperty("BASE_PYTHON_URL"))
                    .trainModel(10)
                    .execute();
        } catch (Exception e) {
            throw new PythonErrorException("Error during using python module");
        }
    }
}
