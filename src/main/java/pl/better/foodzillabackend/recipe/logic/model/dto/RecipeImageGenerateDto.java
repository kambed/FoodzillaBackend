package pl.better.foodzillabackend.recipe.logic.model.dto;

import java.util.List;

public record RecipeImageGenerateDto(
    List<String> generatedImgs,
    String generatedImgsFormat
) {
}
