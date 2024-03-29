package pl.better.foodzillabackend.recipe.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.listener.RateCalculateListener;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
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
@EqualsAndHashCode(exclude = {"reviews", "ingredients", "tags", "customersWhoViewedRecently"})
@Entity
@EntityListeners(RateCalculateListener.class)
public class Recipe {
    public static final String INGREDIENTS = "ingredients";
    public static final String TAGS = "tags";
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
    private String image;
    private double rating;

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

    @ManyToMany(mappedBy = "recentlyViewedRecipes", cascade = CascadeType.REMOVE)
    private Set<Customer> customersWhoViewedRecently = new HashSet<>();

    @ManyToMany(mappedBy = "favouriteRecipes", cascade = CascadeType.REMOVE)
    private Set<Customer> customers = new HashSet<>();
}


