package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecipeSearchService {
    private final EntityManagerFactory entityManagerFactory;

    public SearchResultDto search(SearchPojo input) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Recipe> criteriaQuery = criteriaBuilder.createQuery(Recipe.class);
            Root<Recipe> root = criteriaQuery.from(Recipe.class);
            criteriaQuery.select(root);

            List<Recipe> results = entityManager.createQuery(criteriaQuery).getResultList();

            return SearchResultDto.builder()
                    .currentPage(input.currentPage())
                    .numberOfPages(1)
                    .recipes(Set.of())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
