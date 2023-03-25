package pl.better.foodzillabackend.user.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<Review> reviews = new HashSet<>();
}
