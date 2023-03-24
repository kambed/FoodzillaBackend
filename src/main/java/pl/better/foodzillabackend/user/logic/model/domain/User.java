package pl.better.foodzillabackend.user.logic.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    @OneToMany(mappedBy = "user")
    private Set<Review> reviews = new HashSet<>();
}
