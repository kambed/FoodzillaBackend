package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeImageGenerateDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final Environment environment;
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id: %s not found";

    public RecipeDto getRecipeById(long id) {
        Recipe r = recipeRepository.getRecipeDetailsById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RecipeNotFoundException(
                        RECIPE_NOT_FOUND_MESSAGE.formatted(id)
                ));
        if (r.getImage() == null) {
            r = setImageForAnRecipeWithStableDiffusion(r);
        }
        return recipeDtoMapper.apply(r);
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
        recipe = setImageForAnRecipeWithStableDiffusion(recipe);
        recipeRepository.saveAndFlush(recipe);
        return recipeDtoMapper.apply(recipe);
    }

    public Recipe setImageForAnRecipeWithStableDiffusion(Recipe r) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("text", r.getName());
        map.put("num_images", 1);
        try {
            RecipeImageGenerateDto res = restTemplate.postForObject(environment.getProperty("STABLE_DIFFUSION_API_URL")
                    + "/generate", map, RecipeImageGenerateDto.class);
            if (res != null && res.generatedImgs() != null && res.generatedImgs().size() == 1) {
                r.setImage(res.generatedImgs().get(0));
            }
        } catch (Exception ignored) {
            //ignored
        }
        return r;
    }
}
