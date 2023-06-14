package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailServerControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();
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
    void shouldSendEmail() {
        GraphQlTester.Response res = sendEmail("Example@gmail.com");
        res.path("sendEmail").entity(Customer.class).satisfies(user -> {
            assertEquals("Bob", user.getFirstname());
            assertEquals("Loblaw", user.getLastname());
            assertEquals("BobLoblaw", user.getUsername());
        });
    }

    private GraphQlTester.Response sendEmail(String email) {
        return graphQlTester.documentName("customer-login")
                .variable("email", email)
                .execute();
    }
}
