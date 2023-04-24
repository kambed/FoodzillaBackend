package pl.better.foodzillabackend.review.logic.model.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping
public record CreateReviewCommand(
        @Min(value = 0, message = "Recipe id value cannot be less than 0.")
        Long recipeId,
        @NotNull
        @Size(min = 1, max = 500, message = "Recipe name must be between 1 and 500 characters.")
        String review,
        @Min(value = 0, message = "Rating cannot be less than 0")
        @Max(value = 5, message = "Rating cannot be more than 5")
        Integer rating) {
}
