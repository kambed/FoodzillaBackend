package pl.better.foodzillabackend.utils.rabbitmq.recipeimage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.RecipeShort;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.utils.RecipePromptGenerator;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImagePublisher {

    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final DirectExchange exchange;
    private final MessageConverter converter = new SimpleMessageConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(Priority priority, RecipeDto recipe) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setPriority(priority.getPriorityValue());

        RecipeShort recipeShort = RecipeShort.builder()
                .id(recipe.getId())
                .prompt(RecipePromptGenerator.generatePrompt(recipe))
                .build();

        try {
            asyncRabbitTemplate.sendAndReceive(
                    exchange.getName(),
                    "images",
                    converter.toMessage(objectMapper.writeValueAsString(recipeShort), messageProperties)
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public String sendAndReceive(Priority priority, RecipeDto recipe) {
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
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
