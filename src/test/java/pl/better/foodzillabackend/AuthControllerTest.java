package pl.better.foodzillabackend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private CustomerRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        Customer customer = Customer.builder()
                .firstname("Boob")
                .lastname("obbo")
                .password(passwordEncoder.encode("bOb@4321"))
                .username("Boob123")
                .build();
        repository.saveAndFlush(customer);

        assertEquals(1, repository.findAll().size());
    }

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    public void shouldLoginUserAndReturnTokenForLoggedCustomer() {
        GraphQlTester.Response res = sendLogin("Boob123",
                "bOb@4321");

        res.path("login").entity(Token.class).satisfies(token -> {
            assertEquals("Boob", token.getCustomer().firstname());
            assertEquals("obbo", token.getCustomer().lastname());
            assertEquals("Boob123", token.getCustomer().username());
            assertNotNull(token.getToken());
            assertFalse(token.getToken().isEmpty());
        });
    }

    @Test
    public void shouldReturnErrorThatCustomerDoesNotExist() {
        GraphQlTester.Response res = sendLogin("Rick",
                "s@ncHez321");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "Customer with id: Rick not found"))
                .verify().path("login").valueIsNull();
    }

    private GraphQlTester.Response sendLogin(String username,
                                              String password) {
        return graphQlTester.documentName("customer-login")
                .variable("username", username)
                .variable("password", password)
                .execute();
    }
}
