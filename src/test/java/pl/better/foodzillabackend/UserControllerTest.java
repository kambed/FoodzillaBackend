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

        GraphQlTester.Response res = graphQlTester.documentName("user-create").execute();
        res.path("createCustomer").entity(User.class).satisfies(user -> {
            assertEquals("Bob", user.getFirstname());
            assertEquals("Loblaw", user.getLastname());
            assertEquals("BobLoblaw", user.getUsername());
        });

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnErrorAndAbortAddWhenCreateUserWithIncorrectData() {
        GraphQlTester.Response res = graphQlTester.documentName("user-create-invalid").execute();
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.BAD_REQUEST))
                .verify().path("createCustomer").valueIsNull();
    }

    @Test
    void shouldReturnErrorWhenUserIdNotFound() {
        GraphQlTester.Response res = graphQlTester.documentName("user-get").variable("id", -1).execute();
        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.equals(responseError.getMessage(), "User with id: -1 not found"))
                .verify().path("customer").valueIsNull();
    }

    @Test
    void shouldReturnErrorWhenUserWithGivenUsernameAlreadyExists() {
        User user = User.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bl0bl@w")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());

        GraphQlTester.Response res = graphQlTester.documentName("user-create").execute();

        res.errors().expect(responseError -> responseError.getErrorType().equals(ErrorType.FORBIDDEN) &&
                        Objects.equals(responseError.getMessage(), "User with username: BobLoblaw already exists"))
                .verify().path("createCustomer").valueIsNull();

        assertEquals(1, repository.findAll().size());
    }


    @Test
    void shouldDisplayUserDetailsByGivenId() {
        User user = User.builder()
                .firstname("Booob")
                .lastname("ooobooo")
                .username("obobobo")
                .password("password123")
                .build();
        repository.saveAndFlush(user);
        assertEquals(1, repository.findAll().size());

        GraphQlTester.Response res = graphQlTester.documentName("user-get").variable("id", user.getId()).execute();
        res.errors().verify();
        res.path("customer").entity(User.class).satisfies(recipe -> {
            assertEquals("Booob", user.getFirstname());
            assertEquals("ooobooo", user.getLastname());
            assertEquals("obobobo", user.getUsername());
        });
    }
}
