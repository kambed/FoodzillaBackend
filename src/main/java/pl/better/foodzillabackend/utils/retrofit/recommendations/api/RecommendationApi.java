package pl.better.foodzillabackend.utils.retrofit.recommendations.api;

import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

@Component
public interface RecommendationApi {

    @GET("/train/{epochs}")
    Call<Object> trainModel(@Path("epochs") int epochs);

    @GET("/predict/{user}/{num_of_recommendations}")
    Call<List<Long>> predict(@Path("user") long user, @Path("num_of_recommendations") int numOfRecommendations);
}
