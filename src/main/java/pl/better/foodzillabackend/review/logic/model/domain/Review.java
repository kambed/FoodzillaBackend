package pl.better.foodzillabackend.review.logic.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.user.logic.model.domain.Customer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user","recipe"})
@Entity
public class Review {

    @Id
    private Long id;
    private String review;
    private int rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Customer user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
