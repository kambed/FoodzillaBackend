package pl.better.foodzillabackend.utils.rabbitmq.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.redis.RecipeTemplate;
import pl.better.foodzillabackend.recipe.logic.repository.sql.RecipeRepository;
import pl.better.foodzillabackend.review.logic.repository.ReviewRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecipeConsumer {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeTemplate recipeTemplate;

    @RabbitListener(queues = "recipes")
    public void saveToRedis(Long id) {
        Recipe recipe = recipeRepository.getRecipeDetailsById(id).orElseThrow();
        recipe.setIngredients(null);
        recipe.setTags(null);
        recipe.setReviews(null);
        saveToRedis(recipe);
    }

    public void saveToRedis(Recipe recipe) {
        if (recipe.getIngredients() == null) {
            recipe.setIngredients(getRecipeItems(ingredientRepository.findAllByRecipeId(recipe.getId())));
        }
        if (recipe.getTags() == null) {
            recipe.setTags(getRecipeItems(tagRepository.findAllByRecipeId(recipe.getId())));
        }
        if (recipe.getReviews() == null) {
            recipe.setReviews(getRecipeItems(reviewRepository.findAllByRecipeId(recipe.getId())));
        }
        RecipeDto recipeDto = recipeDtoMapper.apply(recipe);
        recipeDto.setIsFavourite(null);
        recipeTemplate.save(recipeDto);
    }


    private  <T> Set<T> getRecipeItems(List<T> items) {
        if (items.size() == 1 && items.get(0) == null) {
            items = List.of();
        }
        return new HashSet<>(items);
    }
}
