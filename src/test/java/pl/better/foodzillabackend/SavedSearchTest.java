package pl.better.foodzillabackend;

import jakarta.transaction.Transactional;
import okhttp3.mockwebserver.MockResponse;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.search.logic.model.domain.Search;
import pl.better.foodzillabackend.search.logic.model.domain.SearchFilters;
import pl.better.foodzillabackend.search.logic.model.domain.SearchSort;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SavedSearchTest extends TestBase {

    private Customer user;
    private HashSet<SearchFilters> filters1 = new HashSet<>();
    private HashSet<SearchFilters> filters2 = new HashSet<>();
    private HashSet<SearchSort> sort1 = new HashSet<>();
    private Search search1;
    private Search search2;
    private Search search3;

    @BeforeEach
    public void resetDb() {
        super.resetDb();

        filters1 = new HashSet<>();
        filters1.add(SearchFilters.builder().attribute("name").equals("pyszna").build());
        filters2.add(SearchFilters.builder().attribute("name").equals("stek").build());
        filters2.add(SearchFilters.builder().attribute("asdasd").equals("eeeeeeee").build());
        sort1.add(SearchSort.builder().attribute("name").direction("ASC").build());

        search1 = Search.builder()
                .phrase("pyszna jajecznica")
                .build();

        search2 = Search.builder()
                .phrase("pyszny stek")
                .filters(new HashSet<>(filters1))
                .build();

        search3 = Search.builder()
                .phrase("krewetki w sosie śmietanowym")
                .filters(new HashSet<>(filters2))
                .sort(new HashSet<>(sort1))
                .build();

        user = Customer.builder()
                .id(1337L)
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
        searchRepository.saveAllAndFlush(List.of(search1, search2, search3));
        user.setSavedSearches(Set.of(search1, search2, search3));
        customerRepository.saveAndFlush(user);
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnRecipesAllRecipes() {
        GraphQlTester.Response response = graphQlTester.documentName("saved-search-get").execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("savedSearch").entityList(Search.class).satisfies(search -> {
            assertEquals(3, search.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(1)).getName());
        });
    }

//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesAllRecipesPaged() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable("pageSize", 1)
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//
//        response = graphQlTester.documentName("recipe-search")
//                .variable("pageSize", 1)
//                .variable("currentPage", 2)
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesFilteredByPhrase() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable("phrase", "Recipe 1")
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesFilteredByIngredient() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable(
//                        "filters",
//                        Set.of(
//                                Map.of(
//                                        "attribute", "ingredients",
//                                        "in", Set.of("Ingredient 2")
//                                )
//                        )
//                )
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesFilteredByCalories() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable(
//                        "filters",
//                        Set.of(
//                                Map.of(
//                                        "attribute", "calories",
//                                        "from", 50,
//                                        "to", 150
//                                )
//                        )
//                )
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnErrorForFilterNonNumberAttributeByFromTo() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable(
//                        "filters",
//                        Set.of(
//                                Map.of(
//                                        "attribute", "name",
//                                        "from", 150,
//                                        "to", 200
//                                )
//                        )
//                )
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(1, errors.size()));
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesWithEqualDescription() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable(
//                        "filters",
//                        Set.of(
//                                Map.of(
//                                        "attribute", "description",
//                                        "equals", "Description 1"
//                                )
//                        )
//                )
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnRecipesWhichHasOnlyConcreteIngredients() {
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
//                .variable(
//                        "filters",
//                        Set.of(
//                                Map.of(
//                                        "attribute", "ingredients",
//                                        "hasOnly", Set.of("Ingredient 1")
//                                )
//                        )
//                )
//                .execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
//            assertEquals(1, recipes.size());
//            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(0)).getName());
//        });
//    }
//
//    @Test
//    @DirtiesContext
//    void shouldReturnMockedApiOpinion() throws IOException {
//        MockResponse mockResponse = new MockResponse()
//                .setResponseCode(200)
//                .setBody("""
//                        {
//                           "id": "cmpl-uqkvlQyYK7bGYrRHQ0eXlWi7",
//                           "object": "text_completion",
//                           "created": 1589478378,
//                           "model": "ada",
//                           "choices": [
//                             {
//                               "text": "Mocked opinion",
//                               "index": 0,
//                               "logprobs": null,
//                               "finish_reason": "length"
//                             }
//                           ],
//                           "usage": {
//                             "prompt_tokens": 5,
//                             "completion_tokens": 7,
//                             "total_tokens": 12
//                           }
//                         }""");
//        completionsMockWebServer.enqueue(mockResponse);
//
//        GraphQlTester.Response response = graphQlTester.documentName("recipe-search").execute();
//        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
//        response.path("search.opinion").entity(String.class)
//                .satisfies(opinion -> assertEquals("Mocked opinion", opinion));
//
//        completionsMockWebServer.shutdown();
//    }
}
