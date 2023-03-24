package pl.better.foodzillabackend.recipe.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.recipe.logic.model.domain.Tag;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t WHERE t.name = :name")
    List<Tag> findTagByName(String name);
}
