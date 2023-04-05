package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerControllerTest extends TestBase {

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
                "bOb@4321");
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
                "sdaD936245");
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
                .build();
        customerRepository.saveAndFlush(user);
        assertEquals(1, customerRepository.findAll().size());

        GraphQlTester.Response res = sendCreate("Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                        Objects.equals(responseError.getMessage(), "Customer with username: BobLoblaw already exists"))
                .verify().path("createCustomer").valueIsNull();

        assertEquals(1, customerRepository.findAll().size());
    }


    private GraphQlTester.Response sendCreate(String firstname,
                                              String lastname,
                                              String username,
                                              String password) {
        return graphQlTester.documentName("customer-create")
                .variable("firstname", firstname)
                .variable("lastname", lastname)
                .variable("username", username)
                .variable("password", password)
                .execute();
    }

    private GraphQlTester.Response sendEdit(Long id,
                                            String firstname,
                                            String lastname,
                                            String username,
                                            String password) {
        return graphQlTester.documentName("customer-edit")
                .variable("customerId", id)
                .variable("firstname", firstname)
                .variable("lastname", lastname)
                .variable("username", username)
                .variable("password", password)
                .execute();
    }

    @Test
    @Disabled
    void shouldEditUserInDatabaseWithCorrectData() {
        assertEquals(0, customerRepository.findAll().size());
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
        assertEquals(1, customerRepository.findAll().size());
        GraphQlTester.Response res = sendEdit(user.getId(),
                "Tomek",
                "Hajto",
                "RozjechalemBabeNaPasach",
                "bOb@4321");
        res.path("editCustomer").entity(Customer.class).satisfies(userResponse -> {
            assertEquals("Tomek", userResponse.getFirstname());
            assertEquals("Hajto", userResponse.getLastname());
            assertEquals("RozjechalemBabeNaPasach", userResponse.getUsername());
        });

        assertEquals(1, customerRepository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenEditUserWithIncorrectData() {
        assertEquals(0, customerRepository.findAll().size());
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
        GraphQlTester.Response res = sendEdit(user.getId(),
                "b",
                "o",
                "b",
                "sdaD936245");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("editCustomer").valueIsNull();
    }

    @Test
    @Disabled
    void shouldReturnErrorWhenUserDuringEditHasUsernameWhichExist() {
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
        assertEquals(1, customerRepository.findAll().size());

        GraphQlTester.Response res = sendEdit(user.getId(),
                "Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                        Objects.equals(responseError.getMessage(), "Customer with username: BobLoblaw already exists"))
                .verify().path("editCustomer").valueIsNull();

        assertEquals(1, customerRepository.findAll().size());
    }

    @Test
    @Disabled
    void shouldReturnErrorWhileEditWhenUserIdInUpdateUserCommandIdNotFound() {
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        customerRepository.saveAndFlush(user);
        assertEquals(1, customerRepository.findAll().size());
        Long nonExistentId = 112L;
        GraphQlTester.Response res = sendEdit(nonExistentId,
                "Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "Customer with id: "+ nonExistentId + " not found"))
                .verify().path("editCustomer").valueIsNull();

        assertEquals(1, customerRepository.findAll().size());
    }


}
