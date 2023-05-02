package pl.better.foodzillabackend.utils.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.RecipeShort;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.utils.RecipePromptGenerator;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class PublisherMq {

    private final RabbitTemplate rabbitTemplate;
    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final DirectExchange exchange;
    private final MessageConverter converter = new SimpleMessageConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(Priority priority, Recipe recipe) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(priority.getPriorityValue());

        RecipeShort recipeShort = RecipeShort.builder()
                .id(recipe.getId())
                .prompt(RecipePromptGenerator.generatePrompt(recipe))
                .build();

        try {
            rabbitTemplate.convertAndSend(
                    "imageGenerateQueue",
                    converter.toMessage(objectMapper.writeValueAsString(recipeShort), messageProperties)
            );
        } catch (JsonProcessingException e) {
            //ignored
        }
    }

    public String sendAndReceive(Priority priority, Recipe recipe) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(priority.getPriorityValue());

        RecipeShort recipeShort = RecipeShort.builder()
                .id(recipe.getId())
                .prompt(RecipePromptGenerator.generatePrompt(recipe))
                .build();

        try {
            return new String(asyncRabbitTemplate.sendAndReceive(
                    exchange.getName(),
                    "images",
                    converter.toMessage(objectMapper.writeValueAsString(recipeShort), messageProperties)
            ).get().getBody());
        } catch (JsonProcessingException | ExecutionException e) {
            //ignored
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
