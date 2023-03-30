package pl.better.foodzillabackend.utils.retrofit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerateRecipeImageRequestDto(
        String text,
        @JsonProperty("num_images")
        int numImages
) {
}
