package pl.better.foodzillabackend.recipe.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.utils.StringToListConverter;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"reviews", "ingredients", "tags"})
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int timeOfPreparation;
    private int numberOfSteps;
    @Convert(converter = StringToListConverter.class)
    private List<String> steps;
    private int numberOfIngredients;
    private int calories;
    private int fat;
    private int sugar;
    private int sodium;
    private int protein;
    private int saturatedFat;
    private int carbohydrates;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE)
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
