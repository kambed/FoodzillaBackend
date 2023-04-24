package pl.better.foodzillabackend.recipe.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.ingredients i " +
            "LEFT JOIN FETCH r.reviews re " +
            "LEFT JOIN FETCH r.tags t WHERE r.id = :id")
    Optional<Recipe> getRecipeDetailsById(long id);
    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.ingredients i " +
            "LEFT JOIN FETCH r.reviews re " +
            "LEFT JOIN FETCH r.tags t WHERE r.id IN :id")
    List<Recipe> getRecipesIds(List<Long> id);
}
