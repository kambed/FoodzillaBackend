package pl.better.foodzillabackend.recipe.logic.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.redis.RecipeTemplate;
import pl.better.foodzillabackend.recipe.logic.repository.sql.RecipeRepository;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;
import pl.better.foodzillabackend.utils.rabbitmq.recipe.RecipeConsumer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecipeRepositoryAdapter {
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";

    private final RecipeRepository recipeRepository;
    private final RecipeConsumer recipeConsumer;
    private final RecipeTemplate recipeTemplate;
    private final RecipeDtoMapper recipeDtoMapper;

    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public Recipe findById(long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
    }

    public void updateRecipeInRedisById(long id) {
        recipeConsumer.saveToRedis(getRecipeByIdFromSql(id));
    }

    public RecipeDto getRecipeById(long id) {
        return recipeTemplate.getById(id).orElse(recipeDtoMapper.apply(getRecipeByIdFromSql(id)));
    }
    public Recipe getRecipeByIdFromSql(long id) {
        Recipe recipe = recipeRepository.getRecipeDetailsById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));

        recipe.setIngredients(getRecipeItems(ingredientRepository.findAllByRecipeId(recipe.getId())));
        recipe.setTags(getRecipeItems(tagRepository.findAllByRecipeId(recipe.getId())));
        recipe.setReviews(getRecipeItems(reviewRepository.findAllByRecipeId(recipe.getId())));

        return recipe;
    }


    public static <T> Set<T> getRecipeItems(List<T> items) {
        if (items.size() == 1 && items.get(0) == null) {
            items = List.of();
        }
        return new HashSet<>(items);
    }

    public List<RecipeDto> getRecipesByIds(List<Long> ids) {
        List<RecipeDto> recipes = recipeTemplate.getRecipesByIds(ids);
        if (recipes.size() != ids.size()) {
            ids.stream()
                    .filter(id -> recipes.stream().noneMatch(recipe -> recipe.getId().equals(id)))
                    .forEach(id -> recipes.add(recipeDtoMapper.apply(getRecipeByIdFromSql(id))));
        }
        return recipes;
    }

    public void saveAndFlush(Recipe recipe) {
        recipeRepository.saveAndFlush(recipe);
        recipeConsumer.saveToRedis(recipe);
    }

    public void deleteAll() {
        recipeRepository.deleteAll();
        recipeTemplate.deleteAll();
    }

    public Collection<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findAllAndGet(int id) {
        return recipeRepository.findAll().get(id);
    }
}
