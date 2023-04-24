package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IngredientControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();
        Ingredient ingredient1 = new Ingredient("Salt");
        Ingredient ingredient2 = new Ingredient("Water");
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
    }

    @Test
    void shouldReturnAllIngredients() {
        GraphQlTester.Response response = graphQlTester.documentName("ingredients").execute();
        response.errors().verify();
        response.path("ingredients").entityList(IngredientDto.class).satisfies(ingredients -> {
            assertEquals(2, ingredients.size());
            assertTrue(ingredients.stream().anyMatch(ingredient -> ingredient.name().equals("Salt")));
            assertTrue(ingredients.stream().anyMatch(ingredient -> ingredient.name().equals("Water")));
        });
    }
}
