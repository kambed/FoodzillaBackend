package pl.better.foodzillabackend.utils.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitMessageFuture;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.RecipeShort;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.utils.RecipePromptGenerator;

@Component
@RequiredArgsConstructor
public class PublisherMq {

    private final AsyncRabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;
    private final MessageConverter converter = new SimpleMessageConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitMessageFuture sendAndReceive(int priority, Recipe recipe) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(priority);

        RecipeShort recipeShort = RecipeShort.builder()
                .id(recipe.getId())
                .prompt(RecipePromptGenerator.generatePrompt(recipe))
                .build();

        try {
            return rabbitTemplate.sendAndReceive(
                    exchange.getName(),
                    "images",
                    converter.toMessage(objectMapper.writeValueAsString(recipeShort), messageProperties)
            );
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
