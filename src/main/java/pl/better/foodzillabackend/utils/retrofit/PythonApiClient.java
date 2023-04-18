package pl.better.foodzillabackend.utils.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class PythonApiClient {
    private static final String BASE_URL = "http://localhost:5000";

    public static PythonApi create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request requestBuilder = chain.request().newBuilder()
                                    .addHeader("Accept", "application/json")
                                    .build();
                            return chain.proceed(requestBuilder);
                        })
                        .build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(PythonApi.class);
    }
}
