package pl.better.foodzillabackend;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationsControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();

        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
    }
    @Test
    @DirtiesContext
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnMockedRecipeAndOpinion() throws IOException {
        Recipe recipe = Recipe.builder()
                .name("Test recipe")
                .description("Test description")
                .build();
        recipeRepository.saveAndFlush(recipe);

        MockResponse completionsMockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                           "id": "cmpl-uqkvlQyYK7bGYrRHQ0eXlWi7",
                           "object": "text_completion",
                           "created": 1589478378,
                           "model": "ada",
                           "choices": [
                             {
                               "text": "Mocked opinion",
                               "index": 0,
                               "logprobs": null,
                               "finish_reason": "length"
                             }
                           ],
                           "usage": {
                             "prompt_tokens": 5,
                             "completion_tokens": 7,
                             "total_tokens": 12
                           }
                         }""");
        completionsMockWebServer.enqueue(completionsMockResponse);

        MockResponse recommendationsMockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody("[%s]".formatted(recipe.getId()));
        recommendationsMockWebServer.enqueue(recommendationsMockResponse);
        recommendationsMockWebServer.enqueue(recommendationsMockResponse);
        recommendationsMockWebServer.enqueue(recommendationsMockResponse);

        GraphQlTester.Response response = graphQlTester.documentName("recommendations").execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("recommendations.opinion").entity(String.class)
                .satisfies(opinion -> assertEquals("Mocked opinion", opinion));
        response.path("recommendations.recipes").entityList(RecipeDto.class)
                .satisfies(recipeDtos -> {
                    assertEquals(1, recipeDtos.size());
                    assertEquals(recipe.getId(), recipeDtos.get(0).getId());
                    assertEquals("Test recipe", recipeDtos.get(0).getName());
                });

        completionsMockWebServer.shutdown();
    }
}
