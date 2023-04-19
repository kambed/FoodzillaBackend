package pl.better.foodzillabackend.utils.retrofit.completions.api;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CompletionsAdapter {
    private final Environment environment;

    public String generateCompletion(String prompt) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(environment.getRequiredProperty("COMPLETIONS_API_URL"))
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request requestBuilder = chain.request()
                                    .newBuilder()
                                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        try {
            return Objects.requireNonNull(
                    retrofit.create(OpenAiAPI.class)
                            .generateCompletion(new OpenAiCompletionsRequestDto("ada", prompt, 100))
                            .execute()
                            .body()
                    )
                    .choices()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .text();
        } catch (IOException e) {
            return null;
        }
    }
}
