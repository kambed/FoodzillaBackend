package pl.better.foodzillabackend.recipe.logic.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.redis.RecipeTemplate;
import pl.better.foodzillabackend.recipe.logic.repository.sql.RecipeRepository;
import pl.better.foodzillabackend.utils.rabbitmq.recipe.RecipeConsumer;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecipeRepositoryAdapter {
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";

    private final RecipeRepository recipeRepository;
    private final RecipeConsumer recipeConsumer;
    private final RecipeTemplate recipeTemplate;

    public Recipe findById(long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
    }

    public RecipeDto getRecipeById(long id) {
        return recipeTemplate.getById(id).orElseThrow(() -> new RecipeNotFoundException(
                RECIPE_NOT_FOUND_MESSAGE.formatted(id)
        ));
    }

    public List<RecipeDto> getRecipesByIds(List<Long> ids) {
        return recipeTemplate.getRecipesByIds(ids);
    }

    public void saveAndFlush(Recipe recipe) {
        recipeRepository.saveAndFlush(recipe);
        recipeConsumer.saveToRedis(recipe);
    }

    public void deleteAll() {
        recipeRepository.deleteAll();
    }

    public Collection<Recipe> findAll() {
        return recipeRepository.findAll();
    }
}
