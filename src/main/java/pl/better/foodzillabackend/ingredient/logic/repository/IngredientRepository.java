package pl.better.foodzillabackend.ingredient.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query("SELECT i FROM Ingredient i WHERE i.name = :name")
    List<Ingredient> findIngredientByName(String name);

    @Query("SELECT i FROM Recipe r LEFT JOIN r.ingredients i WHERE r.id = :id")
    List<Ingredient> findAllByRecipeId(long id);
}
