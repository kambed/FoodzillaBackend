package pl.better.foodzillabackend.tag.logic.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "recipes")
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.REMOVE)
    private Set<Recipe> recipes = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }
}
