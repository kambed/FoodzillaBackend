package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.exceptions.type.FilterInputException;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepositoryAdapter;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
import pl.better.foodzillabackend.utils.rabbitmq.Priority;
import pl.better.foodzillabackend.utils.rabbitmq.recipeimage.ImagePublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecipeSearchService {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<Long> criteriaQuery;
    private final Root<Recipe> root;
    private final Map<String, Join<Recipe, ?>> joins;
    private final RecipeRepositoryAdapter recipeRepository;
    private final ImagePublisher imagePublisher;
    public RecipeSearchService(
            EntityManagerFactory entityManagerFactory,
            RecipeRepositoryAdapter recipeRepository,
            ImagePublisher imagePublisher
    ) {
        entityManager = entityManagerFactory.createEntityManager();
        this.imagePublisher = imagePublisher;
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Long.class);
        root = criteriaQuery.from(Recipe.class);
        joins = Map.of(
                Recipe.INGREDIENTS, root.join(Recipe.INGREDIENTS, JoinType.LEFT),
                Recipe.TAGS, root.join(Recipe.TAGS, JoinType.LEFT)
        );
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public SearchResultDto search(SearchPojo input) {

        Pageable pageable = PageRequest.of(input.currentPage() - 1, input.pageSize());
        List<Predicate> predicates = new ArrayList<>();
        predicates.addAll(getPhrasePredicates(input));
        predicates.addAll(getFiltersPredicates(input));
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

        List<Order> orders = getOrders(input);
        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }
        criteriaQuery.select(root.get("id"));
        criteriaQuery.distinct(true);
        List<Long> results = entityManager.createQuery(criteriaQuery).getResultList();

        List<Long> recipeIds = results.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        List<RecipeDto> recipes = recipeRepository.getRecipesByIds(recipeIds);

        Page<RecipeDto> page = new PageImpl<>(recipes, pageable, results.size());

        List<Long> recipeNextPageIds = results.stream()
                .skip(pageable.getOffset() + pageable.getPageSize())
                .limit(pageable.getPageSize())
                .toList();

        recipeNextPageIds.forEach(
                id -> imagePublisher.send(Priority.LOW, recipeRepository.getRecipeById(id))
        );

        return SearchResultDto.builder()
                .currentPage(page.getNumber() + 1)
                .numberOfPages(page.getTotalPages())
                .recipes(page.getContent())
                .build();
    }

    private List<Predicate> getPhrasePredicates(SearchPojo input) {
        if (input.phrase() == null) {
            return List.of();
        }
        String searchPhrase = "%" + input.phrase().toLowerCase() + "%";
        return List.of(criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPhrase),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPhrase)
        ));
    }

    private List<Predicate> getFiltersPredicates(SearchPojo input) {
        if (input.filters() == null) {
            return List.of();
        }
        List<Predicate> predicates = new ArrayList<>();
        input.filters().forEach(filter -> {
            Path<Object> path = resolvePath(filter.getAttribute(), joins);
            String type = path.getModel().getBindableJavaType().getName();
            if (!type.equals("int") && (filter.getFrom() != null || filter.getTo() != null)) {
                throw new FilterInputException(
                        "Cannot filter by range on non-number attribute " + filter.getAttribute() + "."
                );
            }
            if (filter.getEquals() != null) {
                predicates.add(criteriaBuilder.equal(path, filter.getEquals()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getAttribute()), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(filter.getAttribute()), filter.getTo()));
            }
            if (filter.getIn() != null) {
                predicates.add(path.in(filter.getIn()));
            }
            if (filter.getHasOnly() != null) {
                predicates.add(getHasOnlyPredicate(filter));
            }
        });
        return predicates;
    }

    private Predicate getHasOnlyPredicate(RecipeFilterPojo filter) {
        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<Recipe> subRoot = subquery.from(Recipe.class);
        Join<Recipe, Ingredient> subIngredientsJoin = subRoot.join(Recipe.INGREDIENTS, JoinType.LEFT);
        Join<Recipe, Tag> subTagsJoin = subRoot.join(Recipe.TAGS, JoinType.LEFT);
        Path<Object> hasOnlyPath = resolvePath(
                filter.getAttribute(),
                Map.of(
                        Recipe.INGREDIENTS, subIngredientsJoin,
                        Recipe.TAGS, subTagsJoin
                )
        );

        subquery.select(subRoot.get("id"))
                .where(criteriaBuilder.or(
                        criteriaBuilder.not(hasOnlyPath.in(filter.getHasOnly())),
                        criteriaBuilder.isNull(hasOnlyPath)
                ))
                .distinct(true);
        return criteriaBuilder.not(root.get("id").in(subquery));
    }

    private Path<Object> resolvePath(String attribute, Map<String, Join<Recipe, ?>> joins) {
        if (joins.containsKey(attribute)) {
            return joins.get(attribute).get("name");
        }
        return root.get(attribute);
    }

    private List<Order> getOrders(SearchPojo input) {
        if (input.sort() == null) {
            return List.of();
        }
        List<Order> orders = new ArrayList<>();
        input.sort().forEach(sort -> {
            if (sort.getDirection().equals(SortDirectionPojo.ASC)) {
                orders.add(criteriaBuilder.asc(root.get(sort.getAttribute())));
            } else {
                orders.add(criteriaBuilder.desc(root.get(sort.getAttribute())));
            }
        });
        return orders;
    }
}
