package pl.better.foodzillabackend.utils.retrofit.model;

public record GenerateRecipeImageRequestDto(
        String text,
        int num_images
) {
}
