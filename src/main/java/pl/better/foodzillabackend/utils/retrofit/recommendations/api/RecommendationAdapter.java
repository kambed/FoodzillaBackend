package pl.better.foodzillabackend.utils.retrofit.recommendations.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RecommendationAdapter {
    private final String url;
    private final int numOfRecommendations;
    private final int epochs;

    public RecommendationAdapter(Environment environment) {
        this.url = environment.getRequiredProperty("RECOMMENDATIONS_API_URL");
        this.numOfRecommendations = environment.getRequiredProperty(
                "NUM_OF_RECOMMENDATIONS",
                Integer.class
        );
        this.epochs = environment.getRequiredProperty(
                "NUM_OF_EPOCHS",
                Integer.class
        );
    }


    public List<Long> getRecommendations(long userId) throws IOException {
        return createApi()
                .predict(userId, numOfRecommendations)
                .execute()
                .body();
    }

    public synchronized void train() throws IOException {
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
                                    .addHeader(HttpHeaders.ACCEPT, "application/json")
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(RecommendationApi.class);
    }
}
