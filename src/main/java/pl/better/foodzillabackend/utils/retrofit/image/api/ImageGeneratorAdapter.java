package pl.better.foodzillabackend.utils.retrofit.image.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.domain.RecipeShort;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageResponseDto;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ImageGeneratorAdapter {
    private final String url;
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImageGeneratorAdapter(Environment environment, RecipeRepository recipeRepository) {
        this.url = environment.getRequiredProperty("STABLE_DIFFUSION_API_URL");
        this.recipeRepository = recipeRepository;
    }

    @RabbitListener(queues = "imageGenerateQueue")
    @Async("rabbitMqTaskExecutor")
    public synchronized CompletableFuture<String> generateImage(String recipeJson) throws JsonProcessingException {
        RecipeShort recipe = objectMapper.readValue(recipeJson, RecipeShort.class);
        log.info("Generating image for recipe: {}", recipe.prompt());
        Recipe recipeInDb = recipeRepository.findById(recipe.id()).orElseThrow();
        if (recipeInDb.getImage() != null) {
            return CompletableFuture.completedFuture(null);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request requestBuilder = chain.request().newBuilder()
                                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        StableDiffusionAPI stableDiffusionAPI = retrofit.create(StableDiffusionAPI.class);

        try {
            Response<GenerateRecipeImageResponseDto> response = stableDiffusionAPI
                    .generateImage(new GenerateRecipeImageRequestDto(recipe.prompt(), 1))
                            .execute();
            if (!response.isSuccessful() || response.body() == null) {
                return CompletableFuture.completedFuture(null);
            }
            String image = response.body().generatedImgs().get(0);
            recipeInDb.setImage(image);
            recipeRepository.saveAndFlush(recipeInDb);
            return CompletableFuture.completedFuture(image);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
