package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

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
}
