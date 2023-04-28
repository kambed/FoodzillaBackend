package pl.better.foodzillabackend.recipe.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r WHERE r.id = :id")
    Optional<Recipe> getRecipeDetailsById(long id);

    @Query("SELECT r FROM Recipe r WHERE r.id IN :id")
    List<Recipe> getRecipesSummarizationIds(List<Long> id);

    @Query("SELECT r FROM Recipe r LEFT JOIN FETCH r.ingredients i WHERE r.id = :id")
    Optional<Recipe> getRecipeByIdWithIngredients(long id);
}
