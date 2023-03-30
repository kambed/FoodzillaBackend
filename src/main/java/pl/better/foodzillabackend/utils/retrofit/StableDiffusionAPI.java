package pl.better.foodzillabackend.utils.retrofit;

import pl.better.foodzillabackend.utils.retrofit.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.model.GenerateRecipeImageResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StableDiffusionAPI {
    @POST("/generate")
    Call<GenerateRecipeImageResponseDto> generateImage(@Body GenerateRecipeImageRequestDto requestDto);
}
