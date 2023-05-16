package pl.better.foodzillabackend.utils.rabbitmq.recipe;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(Long id) {
        rabbitTemplate.convertAndSend("recipes", id);
    }
}
