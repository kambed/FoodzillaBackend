package pl.better.foodzillabackend.utils.rabbitmq.email;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(Customer customer) {
        rabbitTemplate.convertAndSend("email", customer);
    }
}
