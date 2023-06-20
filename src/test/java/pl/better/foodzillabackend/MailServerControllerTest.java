package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.testcontainers.containers.GenericContainer;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.email.logic.model.domain.RecoveryCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void shouldReturnTrueSendRequestEmailToChangingPassword() {
        GraphQlTester.Response res = sendChangeEmailRequest("Example@gmail.com");
        res.errors().satisfy(errors -> assertEquals(0, errors.size()));
        Boolean reset = res.path("requestPasswordResetEmail").entity(Boolean.class).get();
        assertTrue(reset);
    }

    @Test
    public void shouldReturnFalseSendRequestEmailToChangingPassword() {
        GraphQlTester.Response res = sendChangeEmailRequest("dupajasia@gmail.com");
        res.errors().satisfy(errors -> assertEquals(0, errors.size()));
        Boolean reset = res.path("requestPasswordResetEmail").entity(Boolean.class).get();
        assertFalse(reset);
    }

    @Test
    public void shouldReturnTrueIfThePasswordIsSuccessfulChange() {
        assertEquals(0, recoveryCodeRepository.count());
        String code = "Yf0EPEqAIR";
        recoveryCodeRepository.saveAndFlush(new RecoveryCode("Example@gmail.com", code));
        assertEquals(1, recoveryCodeRepository.count());

        String oldPassword = customerRepository.findCustomerByEmail("Example@gmail.com").get().getPassword();
        GraphQlTester.Response res = sendNewPassword("Example@gmail.com", code, "newPassword");
        res.errors().satisfy(errors -> assertEquals(0, errors.size()));
        Boolean reset = res.path("resetPassword").entity(Boolean.class).get();
        assertTrue(reset);
        assertEquals(0, recoveryCodeRepository.count());
        String newPassword = customerRepository.findCustomerByEmail("Example@gmail.com").get().getPassword();
        assertNotEquals(newPassword, oldPassword);
    }

    @Test
    public void shouldReturnFalseIfThePasswordIsNotSuccessfulChangeBecauseCodeToChangeWasNotPresent() {
        assertEquals(0, recoveryCodeRepository.count());
        String code = "Yf0EPEqAIR";

        String oldPassword = customerRepository.findCustomerByEmail("Example@gmail.com").get().getPassword();
        GraphQlTester.Response res = sendNewPassword("Example@gmail.com", code, "newPassword");
        res.errors().satisfy(errors -> assertEquals(0, errors.size()));
        Boolean reset = res.path("resetPassword").entity(Boolean.class).get();
        assertFalse(reset);
        String newPassword = customerRepository.findCustomerByEmail("Example@gmail.com").get().getPassword();
        assertEquals(newPassword, oldPassword);
    }

    private GraphQlTester.Response sendChangeEmailRequest(
            String email) {
        return graphQlTester.documentName("email-request-send")
                .variable("email", email)
                .execute();
    }

    private GraphQlTester.Response sendNewPassword(
            String email, String token, String newPassword) {
        return graphQlTester.documentName("email-change-password")
                .variable("email", email)
                .variable("token", token)
                .variable("newPassword", newPassword)
                .execute();
    }
}
