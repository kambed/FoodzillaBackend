package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeSearchControllerTest extends TestBase {
    @BeforeEach
    public void resetDb() {
        super.resetDb();
        Ingredient ingredient1 = Ingredient.builder()
                .name("Ingredient 1")
                .build();
        Ingredient ingredient2 = Ingredient.builder()
                .name("Ingredient 2")
                .build();
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);

        Recipe recipe1 = Recipe.builder()
                .name("Recipe 1")
                .description("Description 1")
                .calories(100)
                .ingredients(Set.of(ingredient1, ingredient2))
                .tags(Set.of())
                .reviews(Set.of())
                .build();

        Recipe recipe2 = Recipe.builder()
                .name("Recipe 2")
                .description("Description 2")
                .calories(200)
                .ingredients(Set.of(ingredient1))
                .tags(Set.of())
                .reviews(Set.of())
                .build();

        recipeRepository.saveAndFlush(recipe1);
        recipeRepository.saveAndFlush(recipe2);
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesAllRecipes() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search").execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(2, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(1)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesAllRecipesPaged() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable("pageSize", 1)
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
        });

        response = graphQlTester.documentName("recipe-search")
                .variable("pageSize", 1)
                .variable("currentPage", 2)
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesFilteredByPhrase() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable("phrase", "Recipe 1")
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesFilteredByIngredient() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable(
                        "filters",
                        Set.of(
                                Map.of(
                                        "attribute", "ingredients",
                                        "in", Set.of("Ingredient 2")
                                )
                        )
                )
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesFilteredByCalories() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable(
                        "filters",
                        Set.of(
                                Map.of(
                                        "attribute", "calories",
                                        "from", 50,
                                        "to", 150
                                )
                        )
                )
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnErrorForFilterNonNumberAttributeByFromTo() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable(
                        "filters",
                        Set.of(
                                Map.of(
                                        "attribute", "name",
                                        "from", 150,
                                        "to", 200
                                )
                        )
                )
                .execute();
        response.errors().satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesWithEqualDescription() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable(
                        "filters",
                        Set.of(
                                Map.of(
                                        "attribute", "description",
                                        "equals", "Description 1"
                                )
                        )
                )
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 1", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }

    @Test
    @DirtiesContext
    void shouldReturnRecipesWhichHasOnlyConcreteIngredients() {
        GraphQlTester.Response response = graphQlTester.documentName("recipe-search")
                .variable(
                        "filters",
                        Set.of(
                                Map.of(
                                        "attribute", "ingredients",
                                        "hasOnly", Set.of("Ingredient 1")
                                )
                        )
                )
                .execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("search.recipes").entityList(Recipe.class).satisfies(recipes -> {
            assertEquals(1, recipes.size());
            assertEquals("Recipe 2", Objects.requireNonNull(recipes.get(0)).getName());
        });
    }
}
