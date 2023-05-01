package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.listener.RecentlyViewedRecipesEvent;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;
import pl.better.foodzillabackend.utils.rabbitmq.PublisherMq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";
    private final ReviewRepository reviewRepository;
    private final PublisherMq publisherMq;

    @Transactional
    public RecipeDto getRecipeById(long id) {
        Recipe recipe = recipeRepository.getRecipeDetailsById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
        recipe.setIngredients(getRecipeItems(ingredientRepository.findAllByRecipeId(id)));
        recipe.setTags(getRecipeItems(tagRepository.findAllByRecipeId(id)));
        recipe.setReviews(getRecipeItems(reviewRepository.findAllByRecipeId(id)));
        publishCustomEvent(recipe);
        return recipeDtoMapper.apply(recipe);
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

    public String getRecipeImageById(RecipeDto recipe, int priority) {
        Recipe r = recipeRepository.getRecipeByIdWithIngredients(recipe.id()).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(recipe.id())
        ));
        if (r.getImage() == null) {
            String ingredients = r
                    .getIngredients()
                    .stream()
                    .map(Ingredient::getName)
                    .collect(Collectors.joining(","));
            try {
                r.setImage(
                        new String(publisherMq.sendAndReceive(priority, String.format("%s made from: %s", r.getName(), ingredients)).get().getBody())
                );
                recipeRepository.saveAndFlush(r);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return r.getImage();
    }

    private void publishCustomEvent(Recipe recipe) {
        RecentlyViewedRecipesEvent recentlyViewedRecipesEvent = new RecentlyViewedRecipesEvent(this, recipe);
        applicationEventPublisher.publishEvent(recentlyViewedRecipesEvent);
    }
}
