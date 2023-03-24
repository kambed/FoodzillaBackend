package pl.better.foodzillabackend.user.logic.model.domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
