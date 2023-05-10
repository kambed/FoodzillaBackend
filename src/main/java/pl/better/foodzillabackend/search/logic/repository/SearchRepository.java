package pl.better.foodzillabackend.search.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.search.logic.model.domain.Search;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> { }

