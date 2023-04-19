package pl.better.foodzillabackend.utils.retrofit.image.api.model;

import java.util.List;

public record GenerateRecipeImageResponseDto(
    List<String> generatedImgs,
    String generatedImgsFormat
) {
}
