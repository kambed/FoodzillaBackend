package pl.better.foodzillabackend.customer.logic.model.domain;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.better.foodzillabackend.auth.logic.model.domain.Role;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.review.logic.model.domain.Review;
import pl.better.foodzillabackend.search.logic.model.domain.Search;

import java.util.*;

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
    private String email;

    @Type(JsonStringType.class)
    private List<Long> recommendations;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "customer_recipe",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> favouriteRecipes = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "recently_viewed_recipes",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> recentlyViewedRecipes = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "user_saved_searches",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "search_id"))
    private Set<Search> savedSearches = new HashSet<>();

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
