package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecentlyViewedRecipesTest extends TestBase {

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
    void shouldDisplayRecentlyViewedRecipes() {
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

        GraphQlTester.Response res1 = graphQlTester.documentName("recipe-get").variable("id", r.getId()).execute();
        res1.errors().verify();

        GraphQlTester.Response res2 = graphQlTester.documentName("recipe-get-recently-viewed").execute();
        res2.errors().verify();
        res2.path("recentlyViewedRecipes").entityList(Recipe.class).satisfies(recipe -> {
            assertEquals(1, recipe.size());
        });
    }
}
