package pl.better.foodzillabackend.recipe.logic.listener;

import org.springframework.context.ApplicationEvent;
import pl.better.foodzillabackend.recipe.logic.model.domain.Recipe;

public class RecentlyViewedRecipesEvent extends ApplicationEvent {
    private final Recipe recipe;

    public RecentlyViewedRecipesEvent(Object source, Recipe recipe) {
        super(source);
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
