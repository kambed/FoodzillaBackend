package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
class FavouriteRecipeControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();

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

        Recipe r2 = Recipe.builder()
                .name("Name2")
                .description("Description2")
                .timeOfPreparation(2)
                .numberOfSteps(2)
                .steps(List.of("Step12", "Step22"))
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
        Customer customer = Customer.builder()
                .firstname("Andree")
                .lastname("eerdna")
                .username("Andree")
                .password(passwordEncoder.encode("password"))
                .favouriteRecipes(Set.of(r))
                .build();

        recipeRepository.saveAndFlush(r);
        recipeRepository.saveAndFlush(r2);
        customerRepository.saveAndFlush(customer);
    }


    @Test
    @WithMockUser(username = "Andree")
    void shouldDisplayCustomersFavouriteRecipes() {
        GraphQlTester.Response res = graphQlTester.documentName("favourite-recipe-get")
                .execute();

        res.path("favouriteRecipes").entityList(Recipe.class).satisfies(result ->
                assertEquals(1, result.size()));
    }

    //FIXME: WORKFLOW BUG
//    @Test
//    @WithMockUser(username = "Andree")
//    void shouldAddRecipeToCustomersFavouriteRecipes() {
//        GraphQlTester.Response res = graphQlTester.documentName("favourite-recipe-add")
//                .variable("recipeId",4)
//                .execute();
//        res.path("addRecipeToFavourites").entityList(Recipe.class).satisfies(result ->
//                assertEquals(2, result.size()));
//    }

    //FIXME: WORKFLOW BUG
//    @Test
//    @WithMockUser(username = "Andree")
//    void shouldRemoveRecipeFromCustomersFavouriteRecipes() {
//        GraphQlTester.Response res = graphQlTester.documentName("favourite-recipe-remove")
//                .variable("recipeId",5)
//                .execute();
//        res.path("removeRecipeFromFavourites").entityList(Recipe.class).satisfies(result ->
//                assertEquals(0, result.size()));
//    }

}
