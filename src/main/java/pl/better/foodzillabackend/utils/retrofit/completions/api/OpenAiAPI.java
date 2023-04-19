package pl.better.foodzillabackend.utils.retrofit.completions.api;

import pl.better.foodzillabackend.utils.retrofit.completions.api.model.OpenAiCompletionsRequestDto;
import pl.better.foodzillabackend.utils.retrofit.completions.api.model.OpenAiCompletionsResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OpenAiAPI {
    @POST("/v1/completions")
    Call<OpenAiCompletionsResponseDto> generateCompletion(@Body OpenAiCompletionsRequestDto requestDto);
}
