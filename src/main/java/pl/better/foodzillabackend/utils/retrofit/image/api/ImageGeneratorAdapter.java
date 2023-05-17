package pl.better.foodzillabackend.utils.retrofit.image.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageResponseDto;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
@Slf4j
public class ImageGeneratorAdapter {
    private final String url;

    public ImageGeneratorAdapter(Environment environment) {
        this.url = environment.getRequiredProperty("STABLE_DIFFUSION_API_URL");
    }

    public synchronized String generateImage(String prompt) {
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

        return callApiAndGetResponse(prompt, stableDiffusionAPI);
    }

    @Nullable
    private String callApiAndGetResponse(String prompt, StableDiffusionAPI stableDiffusionAPI) {
        try {
            Response<GenerateRecipeImageResponseDto> response = stableDiffusionAPI
                    .generateImage(new GenerateRecipeImageRequestDto(prompt, 1))
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
