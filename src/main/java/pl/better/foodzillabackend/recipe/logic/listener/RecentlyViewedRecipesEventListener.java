package pl.better.foodzillabackend.recipe.logic.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class RecentlyViewedRecipesEventListener implements ApplicationListener<RecentlyViewedRecipesEvent> {

    private final CustomerRepository customerRepository;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";

    @Override
    public void onApplicationEvent(RecentlyViewedRecipesEvent event) {
        Recipe recipe = event.getRecipe();

        String principal = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        if (!principal.equals("anonymousUser")) {
            Customer customer = customerRepository.findByUsername(principal).orElseThrow(
                    () -> new CustomerNotFoundException(CUSTOMER_NOT_FOUND.formatted(principal)));

            customer.getRecentlyViewedRecipes().forEach(
                    recipe1 -> {
                        if (recipe1.getId().equals(recipe.getId())) {
                            customer.getRecentlyViewedRecipes().remove(recipe1);
                        }
                    }
            );
            customerRepository.saveAndFlush(customer);
            IntStream.range(19, customer.getRecentlyViewedRecipes().size()).forEach(i ->
                    customer.getRecentlyViewedRecipes().remove(
                            customer.getRecentlyViewedRecipes().iterator().next()
                    )
            );
            customer.getRecentlyViewedRecipes().add(recipe);
            customerRepository.saveAndFlush(customer);
        }
    }
}
