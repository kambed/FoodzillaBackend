package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.search.logic.model.domain.Search;
import pl.better.foodzillabackend.search.logic.model.domain.SearchFilters;
import pl.better.foodzillabackend.search.logic.model.domain.SearchSort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SavedSearchTest extends TestBase {

    @BeforeEach
    public void resetDb() {
        super.resetDb();

        HashSet<SearchFilters> filters1 = new HashSet<>();
        HashSet<SearchFilters> filters2 = new HashSet<>();
        HashSet<SearchSort> sort1 = new HashSet<>();

        filters1.add(SearchFilters.builder().attribute("name").equals("pyszna").build());
        filters2.add(SearchFilters.builder().attribute("name").equals("stek").build());
        filters2.add(SearchFilters.builder().attribute("asdasd").equals("eeeeeeee").build());
        sort1.add(SearchSort.builder().attribute("name").direction("ASC").build());

        Search search1 = Search.builder()
                .phrase("pyszna jajecznica")
                .build();

        Search search2 = Search.builder()
                .phrase("pyszny stek")
                .filters(new HashSet<>(filters1))
                .build();

        Search search3 = Search.builder()
                .phrase("krewetki w sosie śmietanowym")
                .filters(new HashSet<>(filters2))
                .sort(new HashSet<>(sort1))
                .build();

        Customer user = Customer.builder()
                .firstname("Bob")
                .lastname("Loblaw")
                .username("BobLoblaw")
                .password("b0bL0bl@w")
                .build();

        List<Search> userSearches = List.of(search1, search2, search3);
        searchRepository.saveAllAndFlush(userSearches);
        user.setSavedSearches(new HashSet<>(userSearches));
        customerRepository.saveAndFlush(user);
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnUserSavedSearches() {
        GraphQlTester.Response response = graphQlTester.documentName("saved-search-get").execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("savedSearch").entityList(Search.class).satisfies(search -> {
            assertEquals(3, search.size());
            assertTrue(search.stream().anyMatch(x -> x.getPhrase().equals("pyszna jajecznica")));
            assertTrue(search.stream().anyMatch(x -> x.getPhrase().equals("pyszny stek")));
            assertTrue(search.stream().anyMatch(x -> x.getPhrase().equals("krewetki w sosie śmietanowym")));
        });
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnUserCreatedSearch() {
        GraphQlTester.Response response = graphQlTester.documentName("saved-search-create").execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("addSavedSearch").entity(Search.class).satisfies(search -> {
            assertEquals("search phrase", search.getPhrase());
        });
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnUserErrorCreatedSearch() {
        GraphQlTester.Response response = graphQlTester.documentName("saved-search-create-error").execute();
        response.errors().satisfy(errors -> assertEquals(1, errors.size()));
    }

    @Test
    @WithMockUser(username = "BobLoblaw", password = "b0bL0bl@w")
    void shouldReturnUserDeletedSearch() {
        GraphQlTester.Response response = graphQlTester.documentName("saved-search-delete").variable("id",1).execute();
        response.errors().satisfy(errors -> assertEquals(0, errors.size()));
        response.path("deleteSavedSearch").entity(Search.class).satisfies(search -> {
            assertTrue(search.getPhrase().equals("pyszny stek") || search.getPhrase().equals("pyszna jajecznica") || search.getPhrase().equals("krewetki w sosie śmietanowym"));
        });
    }
}
