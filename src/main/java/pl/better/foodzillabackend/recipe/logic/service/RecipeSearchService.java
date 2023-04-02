package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.exceptions.type.FilterInputException;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecipeSearchService {
    private final EntityManagerFactory entityManagerFactory;
    private final RecipeDtoMapper mapper;

    public SearchResultDto search(SearchPojo input) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Recipe> criteriaQuery = criteriaBuilder.createQuery(Recipe.class);
            Root<Recipe> root = criteriaQuery.from(Recipe.class);

            if (input == null) {
                throw new FilterInputException("Input is null");
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getPhrasePredicates(input, criteriaBuilder, root));
            predicates.addAll(getFiltersPredicates(input, criteriaBuilder, root));
            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }

            List<Order> orders = getOrders(input, criteriaBuilder, root);
            if (!orders.isEmpty()) {
                criteriaQuery.orderBy(orders);
            }
            long totalResults = entityManager.createQuery(criteriaQuery).getResultList().size();

            TypedQuery<Recipe> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult((input.currentPage() - 1) * input.pageSize());
            typedQuery.setMaxResults(input.pageSize());

            List<Recipe> results = typedQuery.getResultList();

            Set<RecipeDto> recipes = results.stream()
                    .map(mapper)
                    .collect(LinkedHashSet::new, Set::add, Set::addAll);

            return SearchResultDto.builder()
                    .currentPage(input.currentPage())
                    .numberOfPages((int) Math.ceil((double) totalResults / input.pageSize()))
                    .recipes(recipes)
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
        if (input.filters() == null) {
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

    private List<Order> getOrders(SearchPojo input, CriteriaBuilder criteriaBuilder, Root<Recipe> root) {
        if (input.sort() == null) {
            return List.of();
        }
        List<Order> orders = new ArrayList<>();
        input.sort().forEach(sort -> {
            if (sort.direction().equals(SortDirectionPojo.ASC)) {
                orders.add(criteriaBuilder.asc(root.get(sort.attribute())));
            } else {
                orders.add(criteriaBuilder.desc(root.get(sort.attribute())));
            }
        });
        return orders;
    }
}
