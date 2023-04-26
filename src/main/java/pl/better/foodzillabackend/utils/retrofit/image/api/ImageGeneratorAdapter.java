package pl.better.foodzillabackend.utils.retrofit.image.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageResponseDto;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class ImageGeneratorAdapter {
    private final String url;

    public ImageGeneratorAdapter(Environment environment) {
        this.url = environment.getRequiredProperty("STABLE_DIFFUSION_API_URL");
    }

    public synchronized String generateImage(Recipe recipe) {
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
            String ingredients = String.join(",", recipe.getIngredients().stream().map(Ingredient::getName).toArray(String[]::new));
            String result = String.format("%s made from: %s", recipe.getName(), ingredients);
            Response<GenerateRecipeImageResponseDto> response = stableDiffusionAPI
                    .generateImage(new GenerateRecipeImageRequestDto(result, 1))
                            .execute();
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            return response.body().generatedImgs().get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
