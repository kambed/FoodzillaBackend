package pl.better.foodzillabackend;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.better.foodzillabackend.containers.GreenMailContainerReusable;
import pl.better.foodzillabackend.containers.MySQLContainerReusable;
import pl.better.foodzillabackend.containers.RabbitMqContainerReusable;
import pl.better.foodzillabackend.containers.RedisContainerReusable;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.email.logic.repository.RecoveryCodeRepository;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.search.logic.repository.SearchRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

import java.io.IOException;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@Testcontainers
public class TestBase {

    @Container
    static MySQLContainer<?> mySQLContainer = MySQLContainerReusable.getInstance();
    @Container
    static GenericContainer<?> redisContainer = RedisContainerReusable.getInstance();
    @Container
    static GenericContainer<?> greenMailGenericContainer = GreenMailContainerReusable.getInstance();
    @Container
    static GenericContainer<?> rabbitMqContainer = RabbitMqContainerReusable.getInstance();

    @Autowired
    protected GraphQlTester graphQlTester;
    @Autowired
    protected RecipeRepositoryAdapter recipeRepository;
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
    @Autowired
    protected RecoveryCodeRepository recoveryCodeRepository;
    protected static MockWebServer completionsMockWebServer;
    protected static MockWebServer recommendationsMockWebServer;

    @BeforeAll
    public static void setup() throws IOException {
        if (completionsMockWebServer == null) {
            completionsMockWebServer = new MockWebServer();
            completionsMockWebServer.start();
            System.setProperty("COMPLETIONS_API_URL", completionsMockWebServer.url("/").toString());
        }
        if (recommendationsMockWebServer == null) {
            recommendationsMockWebServer = new MockWebServer();
            recommendationsMockWebServer.start();
            System.setProperty("RECOMMENDATIONS_API_URL", recommendationsMockWebServer.url("/").toString());
        }
    }

    protected void resetDb() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        tagRepository.deleteAll();
        customerRepository.deleteAll();
        searchRepository.deleteAll();
        recoveryCodeRepository.deleteAll();
    }
}
