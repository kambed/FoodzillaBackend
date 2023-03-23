package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recipe.logic.exception.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.domain.Tag;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id: %s not found";

    public RecipeDto getRecipeById(long id) {
        return recipeRepository.getRecipeDetailsById(id)
                .stream()
                .map(recipeDtoMapper)
                .findFirst()
                .orElseThrow(() -> new RecipeNotFoundException(
                        RECIPE_NOT_FOUND_MESSAGE.formatted(id)
                ));
    }

    public RecipeDto createNewRecipeAndSaveInDb(CreateRecipeCommand command) {
        Set<Ingredient> ingredients = new HashSet<>();
        command.ingredients().forEach(i -> {
            //TODO: use ingredient repo to find existing ingredient or create new if not present
        });
        Set<Tag> tags = new HashSet<>();
        command.tags().forEach(i -> {
            //TODO: use tags repo to find existing tag or create new if not present
        });
        Recipe recipe = new Recipe(
                command.name(),
                command.description(),
                command.timeOfPreparation(),
                command.steps().size(),
                command.steps(),
                ingredients.size(),
                command.calories(),
                command.fat(),
                command.sugar(),
                command.sodium(),
                command.protein(),
                command.saturatedFat(),
                command.carbohydrates(),
                new HashSet<>(),
                ingredients,
                tags
        );
        recipeRepository.saveAndFlush(recipe);
        return recipeDtoMapper.apply(recipe);
    }
}
