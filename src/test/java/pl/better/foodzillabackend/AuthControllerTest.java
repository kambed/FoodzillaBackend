package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.auth.logic.model.domain.Token;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();
        Customer customer = Customer.builder()
                .firstname("Boob")
                .lastname("obbo")
                .password(passwordEncoder.encode("bOb@4321"))
                .username("Boob123")
                .email("Example@gmail.com")
                .build();
        customerRepository.saveAndFlush(customer);
    }

    @Test
    void shouldLoginUserAndReturnTokenForLoggedCustomer() {
        GraphQlTester.Response res = sendLogin("Boob123",
                "bOb@4321");

        res.path("login").entity(Token.class).satisfies(token -> {
            assertEquals("Boob", token.getCustomer().firstname());
            assertEquals("obbo", token.getCustomer().lastname());
            assertEquals("Boob123", token.getCustomer().username());
            assertNotNull(token.getToken());
            assertNotNull(token.getRefreshToken());
            assertFalse(token.getToken().isEmpty());
        });
    }

    @Test
    void shouldReturnErrorThatCustomerDoesNotExist() {
        GraphQlTester.Response res = sendLogin("Rick",
                "s@ncHez321");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "Customer with username Rick not found"))
                .verify().path("login").valueIsNull();
    }

    @Test
    void shouldRefreshToken() {
        GraphQlTester.Response res = sendLogin("Boob123",
                "bOb@4321");

        res.path("login").entity(Token.class).satisfies(token -> {
            assertEquals("Boob", token.getCustomer().firstname());
            assertEquals("obbo", token.getCustomer().lastname());
            assertEquals("Boob123", token.getCustomer().username());
            assertNotNull(token.getToken());
            assertNotNull(token.getRefreshToken());

            GraphQlTester.Response res2 = sendRefresh(token.getRefreshToken());
            res2.path("refreshToken").entity(Token.class).satisfies(cred -> {
                assertEquals("Boob", cred.getCustomer().firstname());
                assertEquals("obbo", cred.getCustomer().lastname());
                assertEquals("Boob123", cred.getCustomer().username());
                assertNotNull(cred.getToken());
                assertNotNull(cred.getRefreshToken());
                assertEquals(cred.getRefreshToken(), token.getRefreshToken());
            });
        });
    }

    private GraphQlTester.Response sendLogin(String username,
                                              String password) {
        return graphQlTester.documentName("customer-login")
                .variable("username", username)
                .variable("password", password)
                .execute();
    }

    private GraphQlTester.Response sendRefresh(String refreshToken) {
        return graphQlTester.documentName("customer-token-refresh")
                .variable("refreshToken", refreshToken)
                .execute();
    }
}
