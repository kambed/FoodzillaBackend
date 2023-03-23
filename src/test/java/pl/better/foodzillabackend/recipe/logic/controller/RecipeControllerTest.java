package pl.better.foodzillabackend.recipe.logic.controller;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeControllerTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Autowired
    private RecipeRepository repository;

    @BeforeEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    void shouldAddRecipeToDatabaseWhenCreateRecipeEndpointUsedWithCorrectData() throws IOException {
        GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/createRecipe.graphql");
        assertTrue(response.isOk());
    }


}