package pl.better.foodzillabackend.utils.retrofit.recommendations.api;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RecommendationAdapter {
    private final Environment environment;
    private final String url = environment.getRequiredProperty("RECOMMENDATIONS_API_URL");
    private final int numOfRecommendations = environment.getRequiredProperty(
            "NUM_OF_RECOMMENDATIONS",
            Integer.class
    );
    private final int epochs = environment.getRequiredProperty(
            "NUM_OF_EPOCHS",
            Integer.class
    );


    public List<Long> getRecommendations(long userId) throws IOException {
        return createApi()
                .predict(userId, numOfRecommendations)
                .execute()
                .body();
    }

    public void train() throws IOException {
        createApi()
                .trainModel(epochs)
                .execute();
    }

    private RecommendationApi createApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            Request requestBuilder = chain.request().newBuilder()
                                    .addHeader("Accept", "application/json")
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(RecommendationApi.class);
    }
}
