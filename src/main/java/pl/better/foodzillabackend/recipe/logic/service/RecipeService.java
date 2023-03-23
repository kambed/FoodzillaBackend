package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recipe.logic.exception.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.recipe.logic.repository.TagRepository;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
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
        Recipe recipe = new Recipe(
                command.name(),
                command.description(),
                command.timeOfPreparation(),
                command.steps().size(),
                command.steps(),
                command.ingredients().size(),
                command.calories(),
                command.fat(),
                command.sugar(),
                command.sodium(),
                command.protein(),
                command.saturatedFat(),
                command.carbohydrates(),
                new HashSet<>(),
                command.ingredients().stream().map(i ->
                        ingredientRepository.findIngredientByName(i.getName()).stream().findFirst().orElseGet(() -> {
                            ingredientRepository.saveAndFlush(i);
                            return i;
                        })
                ).collect(Collectors.toSet()),
                command.tags().stream().map(t ->
                        tagRepository.findTagByName(t.getName()).stream().findFirst().orElseGet(() -> {
                            tagRepository.saveAndFlush(t);
                            return t;
                        })
                ).collect(Collectors.toSet())
        );
        recipeRepository.saveAndFlush(recipe);
        return recipeDtoMapper.apply(recipe);
    }
}
