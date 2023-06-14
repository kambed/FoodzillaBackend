package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();
    }

    @Test
    void shouldAddUserToDatabaseWithCorrectData() {
        assertEquals(0, customerRepository.findAll().size());

        GraphQlTester.Response res = sendCreate("Boob",
                "obbo",
                "Boob123",
                "bOb@4321",
                "Example@gmail.com");
        res.path("createCustomer").entity(Customer.class).satisfies(user -> {
            assertEquals("Boob", user.getFirstname());
            assertEquals("obbo", user.getLastname());
            assertEquals("Boob123", user.getUsername());
        });

        assertEquals(1, customerRepository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenCreateUserWithIncorrectData() {
        GraphQlTester.Response res = sendCreate("b",
                "o",
                "b",
                "sdaD936245",
                "Example@gmail.com");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("createCustomer").valueIsNull();
    }

    @Test
    void shouldReturnErrorWhenUserWithGivenUsernameAlreadyExists() {
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .email("Example@gmail.com")
                .build();
        customerRepository.saveAndFlush(user);
        assertEquals(1, customerRepository.findAll().size());

        GraphQlTester.Response res = sendCreate("Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321",
                "Example@gmail.com");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                        Objects.equals(responseError.getMessage(), "Customer with username BobLoblaw already exists"))
                .verify().path("createCustomer").valueIsNull();

        assertEquals(1, customerRepository.findAll().size());
    }


    private GraphQlTester.Response sendCreate(String firstname,
                                              String lastname,
                                              String username,
                                              String password,
                                              String email) {
        return graphQlTester.documentName("customer-create")
                .variable("firstname", firstname)
                .variable("lastname", lastname)
                .variable("username", username)
                .variable("password", password)
                .variable("email", email)
                .execute();
    }

    private GraphQlTester.Response sendEdit(String firstname,
                                            String lastname,
                                            String username,
                                            String password,
                                            String email) {
        return graphQlTester.documentName("customer-edit")
                .variable("firstname", firstname)
                .variable("lastname", lastname)
                .variable("username", username)
                .variable("password", password)
                .variable("email", email)
                .execute();
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenEditUserWithIncorrectData() {
        assertEquals(0, customerRepository.findAll().size());
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .email("Example@gmail.com")
                .build();
        customerRepository.saveAndFlush(user);
        GraphQlTester.Response res = sendEdit("b",
                "o",
                "b",
                "sdaD936245",
                "Example@gmail.com");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("editCustomer").valueIsNull();
    }

    @Nested
    class LoggedInCustomerControllerTest {

        @BeforeEach
        public void setUp() {
            Customer user = Customer.builder()
                    .firstname("Bob")
                    .lastname("Loblaw")
                    .username("BobLoblaw")
                    .password("b0bL0bl@w")
                    .email("Example@gmail.com")
                    .build();
            customerRepository.saveAndFlush(user);
        }

        @Test
        @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
        void shouldEditUserInDatabaseWithCorrectData() {
            assertEquals(1, customerRepository.findAll().size());

            GraphQlTester.Response res = sendEdit(
                    "Tomek",
                    "Hajto",
                    "RozjechalemBabeNaPasach",
                    "bOb@4321",
                    "Example@gmail.com");
            res.path("editCustomer").entity(Customer.class).satisfies(userResponse -> {
                assertEquals("Tomek", userResponse.getFirstname());
                assertEquals("Hajto", userResponse.getLastname());
                assertEquals("RozjechalemBabeNaPasach", userResponse.getUsername());
            });

            assertEquals(1, customerRepository.findAll().size());
        }

        @Test
        @WithMockUser(username = "Stefania", password = "b0bL0bl@w")
        void shouldReturnErrorWhileEditWhenUsernameInUpdateUserCommandIsNotFound() {
            assertEquals(1, customerRepository.findAll().size());

            GraphQlTester.Response res = sendEdit("Obi-Wan",
                    "Kenobi",
                    "BobLoblaw",
                    "IlovESt@rwars321",
                    "Example@gmail.com");

            res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                            Objects.equals(responseError.getMessage(), "Customer with username Stefania not found"))
                    .verify().path("editCustomer").valueIsNull();

            assertEquals(1, customerRepository.findAll().size());
        }

        @Test
        @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
        void shouldReturnErrorWhenUserDuringEditHasUsernameWhichExist() {
            assertEquals(1, customerRepository.findAll().size());
            Customer user = Customer.builder()
                    .firstname("Bob")
                    .lastname("Loblaw")
                    .username("Marian")
                    .password("b0bL0bl@w")
                    .email("Example@gmail.com")
                    .build();
            customerRepository.saveAndFlush(user);
            assertEquals(2, customerRepository.findAll().size());

            GraphQlTester.Response res = sendEdit("Obi-Wan",
                    "Kenobi",
                    "Marian",
                    "IlovESt@rwars321",
                    "Example@gmail.com");

            res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                            Objects.equals(responseError.getMessage(), "Customer with username Marian already exists"))
                    .verify().path("editCustomer").valueIsNull();

            assertEquals(2, customerRepository.findAll().size());
        }
    }
}
