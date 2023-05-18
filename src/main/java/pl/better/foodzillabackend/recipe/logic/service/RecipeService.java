package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.listener.RecentlyViewedRecipesEvent;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.command.CreateRecipeCommand;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;
import pl.better.foodzillabackend.utils.rabbitmq.recipeimage.ImagePublisher;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private static final String ANONYMOUS = "anonymousUser";
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private final RecipeRepositoryAdapter recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final TagRepository tagRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ImagePublisher imagePublisher;

    @Transactional
    public RecipeDto getRecipeById(long id, String principal) {
        RecipeDto recipe = recipeRepository.getRecipeById(id);
        publishCustomEvent(recipe);
        if (!principal.equals(ANONYMOUS)) {
            Customer customer = customerRepository.findByUsername(principal)
                    .orElseThrow(() -> new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND, principal)));
            recipe.setIsFavourite(customer.getFavouriteRecipes()
                    .stream()
                    .anyMatch(rec -> rec.getId() == id));
        }
        return recipe;
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
        RecipeDto r = recipeRepository.getRecipeById(recipe.getId());
        if (r.getImage() == null) {
            r.setImage(imagePublisher.sendAndReceive(priority, r));
        }
        return r.getImage();
    }

    private void publishCustomEvent(RecipeDto recipe) {
        RecentlyViewedRecipesEvent recentlyViewedRecipesEvent = new RecentlyViewedRecipesEvent(
                this, recipeRepository.findById(recipe.getId())
        );
        applicationEventPublisher.publishEvent(recentlyViewedRecipesEvent);
    }
}
