package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.listener.RecentlyViewedRecipesEvent;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.redis.RecipeTemplate;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;
import pl.better.foodzillabackend.utils.rabbitmq.PublisherMq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private static final String ANONYMOUS = "anonymousUser";
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RecipeTemplate recipeTemplate;
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";
    private final ReviewRepository reviewRepository;
    private final PublisherMq publisherMq;

    @Transactional
    public RecipeDto getRecipeById(long id, String principal) {
        Recipe recipe = recipeRepository.getRecipeDetailsById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
        recipe.setIngredients(getRecipeItems(ingredientRepository.findAllByRecipeId(id)));
        recipe.setTags(getRecipeItems(tagRepository.findAllByRecipeId(id)));
        recipe.setReviews(getRecipeItems(reviewRepository.findAllByRecipeId(id)));
        publishCustomEvent(recipe);
        RecipeDto recipeDto = recipeDtoMapper.apply(recipe);
        if (principal.equals(ANONYMOUS)) {
            recipeDto.setIsFavourite(null);
        } else {
            Customer customer = customerRepository.findByUsername(principal)
                    .orElseThrow(() -> new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            principal)));
            recipeDto.setIsFavourite(customer.getFavouriteRecipes()
                    .stream()
                    .anyMatch(rec -> rec.getId() == id));
        }
        recipeTemplate.save(recipeDto);
        RecipeDto redisRecipe = recipeTemplate.getById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
        return recipeDto;
    }

    public <T> Set<T> getRecipeItems(List<T> items) {
        if (items.size() == 1 && items.get(0) == null) {
            items = List.of();
        }
        return new HashSet<>(items);
    }

    @Transactional
    public RecipeDto createNewRecipeAndSaveInDb(CreateRecipeCommand command) {
        Recipe recipe = Recipe.builder()
                .name(command.name())
                .description(command.description())
                .timeOfPreparation(command.timeOfPreparation())
                .numberOfSteps(command.steps().size())
                .steps(command.steps())
                .numberOfIngredients(command.ingredients().size())
                .calories(command.calories())
                .fat(command.fat())
                .sugar(command.sugar())
                .sodium(command.sodium())
                .protein(command.protein())
                .saturatedFat(command.saturatedFat())
                .carbohydrates(command.carbohydrates())
                .reviews(new HashSet<>())
                .ingredients(command.ingredients().stream().map(i ->
                        ingredientRepository.findIngredientByName(i.getName()).stream().findFirst().orElseGet(() -> {
                            ingredientRepository.saveAndFlush(i);
                            return i;
                        })
                ).collect(Collectors.toSet()))
                .tags(command.tags().stream().map(t ->
                        tagRepository.findTagByName(t.getName()).stream().findFirst().orElseGet(() -> {
                            tagRepository.saveAndFlush(t);
                            return t;
                        })
                ).collect(Collectors.toSet()))
                .build();
        recipeRepository.saveAndFlush(recipe);
        return recipeDtoMapper.apply(recipe);
    }

    public String getRecipeImageById(RecipeDto recipe, Priority priority) {
        if (recipe.getImage() != null) {
            return recipe.getImage();
        }
        Recipe r = recipeRepository.getRecipeByIdWithIngredients(recipe.getId()).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(recipe.getId())
        ));
        if (r.getImage() == null) {
            r.setImage(publisherMq.sendAndReceive(priority, r));
            recipeRepository.saveAndFlush(r);
        }
        return r.getImage();
    }

    private void publishCustomEvent(Recipe recipe) {
        RecentlyViewedRecipesEvent recentlyViewedRecipesEvent = new RecentlyViewedRecipesEvent(this, recipe);
        applicationEventPublisher.publishEvent(recentlyViewedRecipesEvent);
    }
}
