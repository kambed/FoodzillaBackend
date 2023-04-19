package pl.better.foodzillabackend.utils.retrofit.image.api;

import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageRequestDto;
import pl.better.foodzillabackend.utils.retrofit.image.api.model.GenerateRecipeImageResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StableDiffusionAPI {
    @POST("/generate")
    Call<GenerateRecipeImageResponseDto> generateImage(@Body GenerateRecipeImageRequestDto requestDto);
}
