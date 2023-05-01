package pl.better.foodzillabackend.utils.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitMessageFuture;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherMq {

    private final AsyncRabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;

    public RabbitMessageFuture sendAndReceive(int priority, String message) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(priority);
        MessageConverter converter = new SimpleMessageConverter();

        return rabbitTemplate.sendAndReceive(
                exchange.getName(),
                "images",
                converter.toMessage(message, messageProperties)
        );
    }
}
