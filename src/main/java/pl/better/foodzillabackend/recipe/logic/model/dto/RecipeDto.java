package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.*;
import pl.better.foodzillabackend.ingredient.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.review.logic.model.dto.ReviewDto;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private int timeOfPreparation;
    private int numberOfSteps;
    private List<String> steps;
    private int numberOfIngredients;
    private int calories;
    private int fat;
    private int sugar;
    private int sodium;
    private int protein;
    private int saturatedFat;
    private int carbohydrates;
    private double rating;
    private String image;
    private Set<ReviewDto> reviews;
    private Set<IngredientDto> ingredients;
    private Set<TagDto> tags;
    private Boolean isFavourite;

    @Override
    public String toString() {
        return "RecipeDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timeOfPreparation=" + timeOfPreparation +
                ", numberOfSteps=" + numberOfSteps +
                ", steps=" + steps +
                ", numberOfIngredients=" + numberOfIngredients +
                ", calories=" + calories +
                ", fat=" + fat +
                ", sugar=" + sugar +
                ", sodium=" + sodium +
                ", protein=" + protein +
                ", saturatedFat=" + saturatedFat +
                ", carbohydrates=" + carbohydrates +
                ", rating=" + rating +
                ", ingredients=" + ingredients +
                ", tags=" + tags +
                '}';
    }
}
