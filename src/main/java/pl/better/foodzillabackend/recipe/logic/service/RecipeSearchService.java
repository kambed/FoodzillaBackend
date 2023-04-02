package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.exceptions.type.FilterInputException;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;

import java.util.ArrayList;
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

            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getPhrasePredicates(input, criteriaBuilder, root));
            predicates.addAll(getFiltersPredicates(input, criteriaBuilder, root));

            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            criteriaQuery.select(root);

            List<Recipe> results = entityManager.createQuery(criteriaQuery).getResultList();

            return SearchResultDto.builder()
                    .currentPage(input.currentPage())
                    .numberOfPages(1)
                    .recipes(Set.of())
                    .build();
        }
    }

    private List<Predicate> getPhrasePredicates(SearchPojo input, CriteriaBuilder criteriaBuilder, Root<Recipe> root) {
        if (input.phrase() == null) {
            return List.of();
        }
        return List.of(criteriaBuilder.or(
                criteriaBuilder.like(root.get("name"), "%" + input.phrase() + "%"),
                criteriaBuilder.like(root.get("description"), "%" + input.phrase() + "%")
        ));
    }

    private List<Predicate> getFiltersPredicates(SearchPojo input, CriteriaBuilder criteriaBuilder, Root<Recipe> root) {
        if (input.filters() != null) {
            return List.of();
        }
        List<Predicate> predicates = new ArrayList<>();
        input.filters().forEach(filter -> {
            boolean isInt = root.get(filter.attribute()).getModel().getBindableJavaType().getName().equals("int");
            if (isInt) {
                root.get(filter.attribute()).as(Integer.class);
            }
            if (!isInt && (filter.from() != null || filter.to() != null)) {
                throw new FilterInputException(
                        "Cannot filter by range on non-number attribute " + filter.attribute() + "."
                );
            }
            if (filter.equals() != null) {
                predicates.add(criteriaBuilder.equal(root.get(filter.attribute()), filter.equals()));
            }
            if (filter.from() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(filter.attribute()), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(filter.attribute()), filter.to()));
            }
            if (filter.in() != null) {
                predicates.add(root.get(filter.attribute()).in(filter.in()));
            }
        });
        return predicates;
    }
}
