package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureGraphQlTester
class IngredientControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeEach
    public void resetDb() {
        ingredientRepository.deleteAll();
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
