package pl.better.foodzillabackend.recipe.logic.model.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import pl.better.foodzillabackend.recipe.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Tag;

import java.util.List;
import java.util.Set;

@SchemaMapping
public record CreateRecipeCommand(
        @NotNull
        @Size(min = 1, max = 250)
        String name,
        String description,
        @Min(0)
        int timeOfPreparation,
        List<String> steps,
        @Min(0)
        int calories,
        @Min(0)
        int fat,
        @Min(0)
        int sugar,
        @Min(0)
        int sodium,
        @Min(0)
        int protein,
        @Min(0)
        int saturatedFat,
        @Min(0)
        int carbohydrates,
        Set<Ingredient> ingredients,
        Set<Tag> tags
) {
}