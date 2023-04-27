package pl.better.foodzillabackend.recipe.logic.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.exceptions.type.FilterInputException;
import pl.better.foodzillabackend.ingredient.logic.model.domain.Ingredient;
import pl.better.foodzillabackend.recipe.logic.mapper.RecipeDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.recipe.logic.model.dto.SearchResultDto;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SavedSearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.SearchPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.SortDirectionPojo;
import pl.better.foodzillabackend.recipe.logic.repository.RecipeRepository;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
import pl.better.foodzillabackend.utils.retrofit.completions.api.CompletionsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RecipeSearchService {

    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private final EntityManager entityManager;
    private final RecipeDtoMapper mapper;
    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<Long> criteriaQuery;
    private final Root<Recipe> root;
    private final Join<Recipe, Ingredient> ingredientsJoin;
    private final Join<Recipe, Tag> tagsJoin;
    private final RecipeRepository recipeRepository;
    private final CompletionsAdapter completionsAdapter;
    private final CustomerRepository customerRepository;

    public RecipeSearchService(
            EntityManagerFactory entityManagerFactory,
            RecipeDtoMapper mapper,
            RecipeRepository recipeRepository,
            CompletionsAdapter completionsAdapter,
            CustomerRepository customerRepository) {
        this.mapper = mapper;
        entityManager = entityManagerFactory.createEntityManager();
        this.customerRepository = customerRepository;
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Long.class);
        root = criteriaQuery.from(Recipe.class);
        ingredientsJoin = root.join("ingredients", JoinType.LEFT);
        tagsJoin = root.join("tags", JoinType.LEFT);
        this.recipeRepository = recipeRepository;
        this.completionsAdapter = completionsAdapter;
    }

    @Transactional
    public SearchResultDto search(SearchPojo input) {

        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Customer customer = getCustomer(principal);
        List<SearchPojo> userSearches = customer.getSearches();
        if (userSearches == null) {
            customer.setSearches(List.of(input));
        } else {
            userSearches.add(input);
            customer.setSearches(userSearches);
        }

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

        List<Recipe> recipes = recipeRepository.getRecipesIds(recipeIds);

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
    
    public List<SearchPojo> getSearches() {
        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Customer customer = getCustomer(principal);
        return customer.getSearches() == null ? new ArrayList<>() : customer.getSearches();
    }

    private List<Predicate> getPhrasePredicates(SearchPojo input) {
        if (input.phrase() == null) {
            return List.of();
        }
        return List.of(criteriaBuilder.or(
                criteriaBuilder.like(root.get("name"), "%" + input.phrase() + "%"),
                criteriaBuilder.like(root.get("description"), "%" + input.phrase() + "%")
        ));
    }

    private List<Predicate> getFiltersPredicates(SearchPojo input) {
        if (input.filters() == null) {
            return List.of();
        }
        List<Predicate> predicates = new ArrayList<>();
        input.filters().forEach(filter -> {
            Path<Object> path = resolvePath(filter.attribute());
            String type = path.getModel().getBindableJavaType().getName();
            boolean isInt = type.equals("int");
            if (isInt) {
                path.as(Integer.class);
            }
            if (!isInt && (filter.from() != null || filter.to() != null)) {
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
        });
        return predicates;
    }

    private Path<Object> resolvePath(String attribute) {
        if (attribute.equals("ingredients")) {
            return ingredientsJoin.get("name");
        }
        if (attribute.equals("tags")) {
            return tagsJoin.get("name");
        }
        return root.get(attribute);
    }

    private Customer getCustomer(String customer) {
        return customerRepository.findByUsername(customer)
                .orElseThrow(() -> {
                    throw new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND,
                            customer));
                });
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
