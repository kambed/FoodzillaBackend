package pl.better.foodzillabackend.recipe.logic.model.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import java.util.List;
import java.util.Set;

@SchemaMapping
public record CreateRecipeCommand(
        @NotNull
        @Size(min = 1, max = 250,  message = "Recipe name must be between 1 and 250 characters.")
        String name,
        String description,
        @Min(value = 0, message = "Time of preparation cannot be less than 0.")
        int timeOfPreparation,
        List<String> steps,
        @Min(value = 0, message = "Calorific value cannot be less than 0.")
        int calories,
        @Min(value = 0, message = "Amount of fat cannot be less than 0.")
        int fat,
        @Min(value = 0, message = "Amount of sugar cannot be less than 0.")
        int sugar,
        @Min(value = 0, message = "Amount of sodium cannot be less than 0.")
        int sodium,
        @Min(value = 0, message = "Amount of protein cannot be less than 0.")
        int protein,
        @Min(value = 0, message = "Amount of saturatedFat cannot be less than 0.")
        int saturatedFat,
        @Min(0)
        int carbohydrates,
        Set<Ingredient> ingredients,
        Set<Tag> tags
) {
}
