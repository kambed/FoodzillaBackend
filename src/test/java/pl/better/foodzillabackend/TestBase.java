package pl.better.foodzillabackend;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.search.logic.repository.SearchRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

import java.io.IOException;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class TestBase {

    @Autowired
    protected GraphQlTester graphQlTester;
    @Autowired
    protected RecipeRepository recipeRepository;
    @Autowired
    protected IngredientRepository ingredientRepository;
    @Autowired
    protected SearchRepository searchRepository;
    @Autowired
    protected TagRepository tagRepository;
    @Autowired
    protected CustomerRepository customerRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    protected static MockWebServer completionsMockWebServer;

    @BeforeAll
    public static void setup() throws IOException {
        if (completionsMockWebServer == null) {
            completionsMockWebServer = new MockWebServer();
            completionsMockWebServer.start();
            System.setProperty("COMPLETIONS_API_URL", completionsMockWebServer.url("/").toString());
        }
    }

    protected void resetDb() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        tagRepository.deleteAll();
        customerRepository.deleteAll();
        searchRepository.deleteAll();
    }
}
