package pl.better.foodzillabackend.recipe.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
