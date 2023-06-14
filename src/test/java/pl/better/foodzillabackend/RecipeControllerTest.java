package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();

        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .email("Example@gmail.com")
                .build();
        customerRepository.saveAndFlush(user);
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldAddRecipeToDatabaseWhenCreateRecipeEndpointUsedWithCorrectData() {
        assertEquals(0, recipeRepository.findAll().size());

        GraphQlTester.Response res = graphQlTester.documentName("recipe-create").execute();
        res.path("createRecipe").entity(Recipe.class).satisfies(recipe -> {
            assertEquals("Salty water", recipe.getName());
            assertEquals("Really salty, perfect for League of Legends players", recipe.getDescription());
            assertEquals(30, recipe.getTimeOfPreparation());
            assertEquals(2, recipe.getNumberOfSteps());
            assertEquals(2, recipe.getSteps().size());
            assertEquals("Get water", recipe.getSteps().get(0));
            assertEquals("Insert salt into water", recipe.getSteps().get(1));
            assertEquals(2, recipe.getNumberOfIngredients());
            assertEquals(100, recipe.getCalories());
            assertEquals(10, recipe.getFat());
            assertEquals(20, recipe.getSugar());
            assertEquals(30, recipe.getSodium());
            assertEquals(40, recipe.getProtein());
            assertEquals(50, recipe.getSaturatedFat());
            assertEquals(60, recipe.getCarbohydrates());
            assertEquals(0, recipe.getReviews().size());
            assertEquals(2, recipe.getIngredients().size());
            assertEquals("water", recipe.getIngredients().stream().toList().get(0).getName().toLowerCase());
            assertEquals("salt", recipe.getIngredients().stream().toList().get(1).getName().toLowerCase());
            assertEquals(1, recipe.getTags().size());
            assertEquals("he he", recipe.getTags().stream().toList().get(0).getName().toLowerCase());
        });

        assertEquals(1, recipeRepository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenCreateRecipeEndpointUsedWithIncorrectData() {
        GraphQlTester.Response res = graphQlTester.documentName("recipe-create-error").execute();
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("createRecipe").valueIsNull();
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldDisplayRecipeDetailsByGivenId() {
        Ingredient i = new Ingredient("Water");
        Tag t = new Tag("Yummy");
        Recipe r = Recipe.builder()
                .name("Name")
                .description("Description")
                .timeOfPreparation(2)
                .numberOfSteps(2)
                .steps(List.of("Step1", "Step2"))
                .numberOfIngredients(1)
                .calories(3)
                .fat(4)
                .sugar(5)
                .sodium(6)
                .protein(7)
                .saturatedFat(8)
                .carbohydrates(9)
                .reviews(new HashSet<>())
                .ingredients(Set.of(
                        ingredientRepository.findIngredientByName(i.getName()).stream().findFirst().orElseGet(() -> {
                            ingredientRepository.saveAndFlush(i);
                            return i;
                        })
                ))
                .tags(Set.of(
                        tagRepository.findTagByName(t.getName()).stream().findFirst().orElseGet(() -> {
                            tagRepository.saveAndFlush(t);
                            return t;
                        })
                )).build();
        recipeRepository.saveAndFlush(r);
        assertEquals(1, recipeRepository.findAll().size());

        GraphQlTester.Response res = graphQlTester.documentName("recipe-get").variable("id", r.getId()).execute();
        res.errors().verify();
        res.path("recipe").entity(Recipe.class).satisfies(recipe -> {
            assertEquals("Name", recipe.getName());
            assertEquals("Description", recipe.getDescription());
            assertEquals(2, recipe.getTimeOfPreparation());
            assertEquals(2, recipe.getNumberOfSteps());
            assertEquals(2, recipe.getSteps().size());
            assertEquals("Step1", recipe.getSteps().get(0));
            assertEquals("Step2", recipe.getSteps().get(1));
            assertEquals(1, recipe.getNumberOfIngredients());
            assertEquals(3, recipe.getCalories());
            assertEquals(4, recipe.getFat());
            assertEquals(5, recipe.getSugar());
            assertEquals(6, recipe.getSodium());
            assertEquals(7, recipe.getProtein());
            assertEquals(8, recipe.getSaturatedFat());
            assertEquals(9, recipe.getCarbohydrates());
            assertEquals(0, recipe.getReviews().size());
            assertEquals(1, recipe.getIngredients().size());
            assertEquals("water", recipe.getIngredients().stream().toList().get(0).getName().toLowerCase());
            assertEquals(1, recipe.getTags().size());
            assertEquals("yummy", recipe.getTags().stream().toList().get(0).getName().toLowerCase());
        });
    }


    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnErrorWhenRecipeIdNotFound() {
        GraphQlTester.Response res = graphQlTester.documentName("recipe-get").variable("id", -1).execute();
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "Recipe with id -1 not found"))
                .verify().path("recipe").valueIsNull();
    }
}