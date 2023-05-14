package pl.better.foodzillabackend.recipe.logic.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.review.logic.model.domain.Review;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
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
    private Set<Review> reviews;
    private Set<Ingredient> ingredients;
    private Set<Tag> tags;
    private Boolean isFavourite;
}
