package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.exceptions.type.FilterInputException;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeSummarizationDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;

import pl.better.foodzillabackend.utils.retrofit.completions.api.CompletionsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecipeSearchService {

    private final EntityManager entityManager;
    private final RecipeSummarizationDtoMapper mapper;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<Long> criteriaQuery;
    private final Root<Recipe> root;
    private final Map<String, Join<Recipe, ?>> joins;
    private final RecipeRepository recipeRepository;
    private final CompletionsAdapter completionsAdapter;
    public RecipeSearchService(
            EntityManagerFactory entityManagerFactory,
            RecipeSummarizationDtoMapper mapper,
            RecipeRepository recipeRepository,
            CompletionsAdapter completionsAdapter) {
        this.mapper = mapper;
        entityManager = entityManagerFactory.createEntityManager();
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Long.class);
        root = criteriaQuery.from(Recipe.class);
        joins = Map.of(
                Recipe.INGREDIENTS, root.join(Recipe.INGREDIENTS, JoinType.LEFT),
                Recipe.TAGS, root.join(Recipe.TAGS, JoinType.LEFT)
        );
        this.recipeRepository = recipeRepository;
        this.completionsAdapter = completionsAdapter;
    }

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

        List<Recipe> recipes = recipeRepository.getRecipesSummarizationIds(recipeIds);

        List<RecipeDto> recipeDtos = recipes.stream()
                .map(mapper)
                .toList();
        Page<RecipeDto> page = new PageImpl<>(recipeDtos, pageable, results.size());

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
            Path<Object> path = resolvePath(filter.attribute(), joins);
            String type = path.getModel().getBindableJavaType().getName();
            if (!type.equals("int") && (filter.from() != null || filter.to() != null)) {
                throw new FilterInputException(
                        "Cannot filter by range on non-number attribute " + filter.attribute() + "."
                );
            }
            if (filter.equals() != null) {
                predicates.add(criteriaBuilder.equal(path, filter.equals()));
            }
            if (filter.from() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(filter.attribute()), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(filter.attribute()), filter.to()));
            }
            if (filter.in() != null) {
                predicates.add(path.in(filter.in()));
            }
            if (filter.hasOnly() != null) {
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
                filter.attribute(),
                Map.of(
                        Recipe.INGREDIENTS, subIngredientsJoin,
                        Recipe.TAGS, subTagsJoin
                )
        );

        subquery.select(subRoot.get("id"))
                .where(criteriaBuilder.or(
                        criteriaBuilder.not(hasOnlyPath.in(filter.hasOnly())),
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
            if (sort.direction().equals(SortDirectionPojo.ASC)) {
                orders.add(criteriaBuilder.asc(root.get(sort.attribute())));
            } else {
                orders.add(criteriaBuilder.desc(root.get(sort.attribute())));
            }
        });
        return orders;
    }

    public String getOpinion(SearchResultDto searchResult) {
        return completionsAdapter.generateCompletion(
                "What do you think about this recipes: " + searchResult.toString()
        );
    }
}
