package pl.better.foodzillabackend.recipe.logic.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.ingredients i " +
            "LEFT JOIN FETCH r.reviews re " +
            "LEFT JOIN FETCH r.tags t WHERE r.id = :id")
    List<Recipe> getRecipeDetailsById(long id);

    @Query(value="SELECT r.id FROM Recipe r", countQuery = "SELECT count(r.id) FROM Recipe r")
    List<Long> getRecipeIds(Pageable pageable);
}
