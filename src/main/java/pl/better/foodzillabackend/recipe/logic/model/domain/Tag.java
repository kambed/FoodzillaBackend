package pl.better.foodzillabackend.recipe.logic.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Recipe> recipes = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }
}
