package pl.better.foodzillabackend.utils.retrofit.completions.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.utils.retrofit.completions.api.model.OpenAiCompletionsRequestDto;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Objects;

@Controller
@Slf4j
public class CompletionsAdapter {
    private final String url;
    private final String apiKey;

    public CompletionsAdapter(Environment environment) {
        this.url = environment.getRequiredProperty("COMPLETIONS_API_URL");
        this.apiKey = environment.getRequiredProperty("COMPLETIONS_API_KEY");
    }

    public String generateCompletion(String prompt) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request requestBuilder = chain.request()
                                    .newBuilder()
                                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                                    .addHeader(
                                            HttpHeaders.AUTHORIZATION,
                                            "Bearer " + apiKey
                                    )
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        OpenAiAPI openAiAPI = retrofit.create(OpenAiAPI.class);
        try {
            return Objects.requireNonNull(
                            openAiAPI
                                    .generateCompletion(new OpenAiCompletionsRequestDto(
                                            prompt,
                                            "ada",
                                            100
                                    ))
                                    .execute()
                                    .body()
                    )
                    .choices()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .text();
        } catch (IOException|NullPointerException e) {
            log.error("Error while calling OpenAI API", e);
            return null;
        }
    }
}
