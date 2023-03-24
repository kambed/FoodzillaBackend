package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;

@SpringBootTest
@AutoConfigureGraphQlTester
class RecipeControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private RecipeRepository repository;

    @BeforeEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    void shouldAddRecipeToDatabaseWhenCreateRecipeEndpointUsedWithCorrectData() {
        //graphQlTester.documentName("recipe.graphql").execute();
    }


}