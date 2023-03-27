package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.user.logic.model.domain.User;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureGraphQlTester
public class UserControllerTest {

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

        GraphQlTester.Response res = send("Boob",
                "obbo",
                "Boob123",
                "bOb@4321");
        res.path("createCustomer").entity(User.class).satisfies(user -> {
            assertEquals("Boob", user.getFirstname());
            assertEquals("obbo", user.getLastname());
            assertEquals("Boob123", user.getUsername());
        });

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenCreateUserWithIncorrectData() {
        GraphQlTester.Response res = send("b",
                "o",
                "b",
                "sdaD936245");
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("createCustomer").valueIsNull();
    }

    @Test
    void shouldReturnErrorWhenUserWithGivenUsernameAlreadyExists() {
        User user = User.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());

        GraphQlTester.Response res = send("Obi-Wan",
                "Kenobi",
                "BobLoblaw",
                "IlovESt@rwars321");

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.FORBIDDEN) &&
                        Objects.equals(responseError.getMessage(), "User with username: BobLoblaw already exists"))
                .verify().path("createCustomer").valueIsNull();

        assertEquals(1, repository.findAll().size());
    }


    private GraphQlTester.Response send(String firstname,
                                        String lastname,
                                        String username,
                                        String password) {
        return graphQlTester.documentName("user-create")
                .variable("firstname", firstname)
                .variable("lastname", lastname)
                .variable("username", username)
                .variable("password", password)
                .execute();
    }
}
