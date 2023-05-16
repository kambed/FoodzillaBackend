package pl.better.foodzillabackend.utils.rabbitmq.recipeimage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.RecipeShort;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.utils.retrofit.image.api.ImageGeneratorAdapter;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageConsumer {
    private final RecipeRepositoryAdapter recipeRepositoryAdapter;
    private final ImageGeneratorAdapter imageGeneratorAdapter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "imageGenerateQueue")
    @Async("rabbitMqTaskExecutor")
    public synchronized CompletableFuture<String> generateImage(String recipeJson) throws JsonProcessingException {
        RecipeShort recipe = objectMapper.readValue(recipeJson, RecipeShort.class);
        log.info("Generating image for recipe: {}", recipe.prompt());
        RecipeDto recipeInDb = recipeRepositoryAdapter.getRecipeById(recipe.id());
        if (recipeInDb.getImage() != null) {
            return CompletableFuture.completedFuture(recipeInDb.getImage());
        }
        try {
            String image = imageGeneratorAdapter.generateImage(recipe.prompt());
            recipeInDb.setImage(image);
            recipeRepositoryAdapter.saveAndFlush(recipeInDb);
            return CompletableFuture.completedFuture(image);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
