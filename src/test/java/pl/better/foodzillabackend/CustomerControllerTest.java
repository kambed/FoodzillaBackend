package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import pl.better.foodzillabackend.user.logic.model.domain.Customer;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class CustomerControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    void shouldAddUserToDatabaseWithCorrectData() {
        assertEquals(0, repository.findAll().size());

        GraphQlTester.Response res = sendCreate("Boob",
                "obbo",
                "Boob123",
                "bOb@4321");
        res.path("createCustomer").entity(Customer.class).satisfies(user -> {
            assertEquals("Boob", user.getFirstname());
            assertEquals("obbo", user.getLastname());
            assertEquals("Boob123", user.getUsername());
        });

        assertEquals(1, repository.findAll().size());
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
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());

        GraphQlTester.Response res = sendCreate("Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.FORBIDDEN) &&
                        Objects.equals(responseError.getMessage(), "User with username: BobLoblaw already exists"))
                .verify().path("createCustomer").valueIsNull();

        assertEquals(1, repository.findAll().size());
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
    void shouldEditUserInDatabaseWithCorrectData() {
        assertEquals(0, repository.findAll().size());
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());
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

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenEditUserWithIncorrectData() {
        assertEquals(0, repository.findAll().size());
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        repository.saveAndFlush(user);
        GraphQlTester.Response res = sendEdit(user.getId(),
                "b",
                "o",
                "b",
                "sdaD936245");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("editCustomer").valueIsNull();
    }

    @Test
    void shouldReturnErrorWhenUserDuringEditHasUsernameWhichExist() {
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());

        GraphQlTester.Response res = sendEdit(user.getId(),
                "Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.FORBIDDEN) &&
                        Objects.equals(responseError.getMessage(), "User with username: BobLoblaw already exists"))
                .verify().path("editCustomer").valueIsNull();

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnErrorWhileEditWhenUserIdInUpdateUserCommandIdNotFound() {
        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());
        Long nonExistentId = 112L;
        GraphQlTester.Response res = sendEdit(nonExistentId,
                "Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "User with id: "+ nonExistentId + " not found"))
                .verify().path("editCustomer").valueIsNull();

        assertEquals(1, repository.findAll().size());
    }


}
