package pl.better.foodzillabackend.search.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.search.logic.model.domain.Search;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query("SELECT s FROM Search s WHERE s.id = :id")
    Optional<Search> getSearchById(long id);

    @Query("SELECT s FROM Search s JOIN UserSavedSearches u WHERE u.customer_id = :id")
    Optional<Set<Search>> getSearchByUserId(long id);
}

