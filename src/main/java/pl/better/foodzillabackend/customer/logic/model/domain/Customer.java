package pl.better.foodzillabackend.customer.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.better.foodzillabackend.auth.model.domain.Role;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.review.logic.model.domain.Review;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@EqualsAndHashCode(exclude = {"reviews","favouriteRecipes","recentlyViewedRecipes"})
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements UserDetails {

    private static final String UNSUPPORTED_OPERATION = "This method is not supported";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "customer_recipe",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private Set<Recipe> favouriteRecipes = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "recently_viewed_recipes",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private Set<Recipe> recentlyViewedRecipes = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(Role.NORMAL);
    }

    @Override
    public boolean isAccountNonExpired() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean isAccountNonLocked() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public boolean isEnabled() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }
}
