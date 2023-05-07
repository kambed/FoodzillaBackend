package pl.better.foodzillabackend.search.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.search.logic.model.domain.Search;

import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query("SELECT s FROM Search s WHERE s.id = :id")
    Optional<Search> getSearchById(long id);
}

