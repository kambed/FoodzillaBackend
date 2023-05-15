package pl.better.foodzillabackend.recipe.logic.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.RecipeNotFoundException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeSummarizationDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteRecipeService {

    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe with id %s not found";
    private final RecipeRepository recipeRepository;
    private final CustomerRepository customerRepository;
    private final RecipeDtoMapper recipeDtoMapper;
    private final RecipeSummarizationDtoMapper recipeSummarizationDtoMapper;

    @Transactional
    public List<RecipeDto> addRecipeToFavourites(String principal, int recipeId) {
        Customer customer = getCustomer(principal);
        Recipe recipe = getRecipe(recipeId);

        customer.getFavouriteRecipes().add(recipe);
        customerRepository.saveAndFlush(customer);

        return customer.getFavouriteRecipes()
                .stream()
                .map(recipeSummarizationDtoMapper)
                .toList();
    }

    @Transactional
    public List<RecipeDto> removeRecipeFromFavourites(String principal, int recipeId) {
        Customer customer = getCustomer(principal);
        Recipe recipe = getRecipe(recipeId);

        customer.getFavouriteRecipes().remove(recipe);
        customerRepository.saveAndFlush(customer);

        return customer.getFavouriteRecipes()
                .stream()
                .map(recipeDtoMapper)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> favouriteRecipes(String principal) {
        Customer customer = getCustomer(principal);
        return customer.getFavouriteRecipes()
                .stream()
                .map(recipeDtoMapper)
                .toList();
    }

    private Customer getCustomer(String customer) {
        return customerRepository.findByUsername(customer)
                .orElseThrow(() -> {
                    throw new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            customer));
                });
    }

    private Recipe getRecipe(int id) {
        return recipeRepository.findById((long) id).orElseThrow(() -> {
            throw new RecipeNotFoundException(String.format(RECIPE_NOT_FOUND_MESSAGE,
                    id));
        });
    }
}
