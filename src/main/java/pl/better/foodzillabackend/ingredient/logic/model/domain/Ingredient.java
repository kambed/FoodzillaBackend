package pl.better.foodzillabackend.ingredient.logic.model.domain;


import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "recipes")
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "ingredients", cascade = CascadeType.REMOVE)
    private Set<Recipe> recipes = new HashSet<>();

    public Ingredient(String name) {
        this.name = name;
    }
}
