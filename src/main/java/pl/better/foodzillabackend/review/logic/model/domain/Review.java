package pl.better.foodzillabackend.review.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"customer", "recipe"})
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String review;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Review(String review, int rating, Customer customer, Recipe recipe) {
        this.review = review;
        this.rating = rating;
        this.customer = customer;
        customer.getReviews().add(this);
        this.recipe = recipe;
        recipe.getReviews().add(this);
    }
}
