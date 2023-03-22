package pl.better.foodzillabackend.recipe.logic.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"reviews", "ingredients", "tags"})
@Entity
public class Recipe {

    @Id
    private Long id;
    private String name;
    private String description;
    private int timeOfPreparation;
    private int numberOfSteps;
    private String steps;
    private int numberOfIngredients;
    private int calories;
    private int fat;
    private int sugar;
    private int sodium;
    private int protein;
    private int saturatedFat;
    private int carbohydrates;

    @OneToMany(mappedBy = "recipe")
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "recipe_tag",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

}
