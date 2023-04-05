package pl.better.foodzillabackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.ingredient.logic.repository.IngredientRepository;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

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
    protected TagRepository tagRepository;
    @Autowired
    protected CustomerRepository customerRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected void resetDb() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        tagRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
