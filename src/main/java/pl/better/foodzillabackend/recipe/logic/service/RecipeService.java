package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;
import pl.better.foodzillabackend.utils.retrofit.ApiBuilder;
import pl.better.foodzillabackend.utils.retrofit.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.model.GenerateRecipeImageResponseDto;
import retrofit2.Response;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeMapper recipeMapper;
    private final Environment environment;
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";

    public RecipeDto getRecipeById(long id) {
        return recipeRepository.getRecipeDetailsById(id)
                .stream()
                .findFirst()
                .map(recipeDtoMapper)
                .orElseThrow(() -> new RecipeNotFoundException(
                        RECIPE_NOT_FOUND_MESSAGE.formatted(id)
                ));
    }

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

    public String getRecipeImageById(RecipeDto recipe) {
        Recipe r = recipeMapper.apply(recipe);
        if (recipe.image() == null) {
            generateImageForRecipe(r);
            recipeRepository.saveAndFlush(r);
        }
        return r.getImage();
    }

    private void generateImageForRecipe(Recipe r) {
        try {
            Response<GenerateRecipeImageResponseDto> res =
                    ApiBuilder.build(environment.getProperty("STABLE_DIFFUSION_API_URL"))
                            .generateImage(new GenerateRecipeImageRequestDto(r.getName(), 1))
                            .execute();
            if (res.isSuccessful() && res.body() != null) {
                r.setImage(res.body().generatedImgs().get(0));
            }
        } catch (Exception ignored) {
            //ignored
        }
    }
}
