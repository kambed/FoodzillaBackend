package pl.better.foodzillabackend.utils.redis.task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.repository.sql.RecipeRepository;
import pl.better.foodzillabackend.utils.rabbitmq.recipe.RecipeProducer;

@Component
@RequiredArgsConstructor
public class SyncRecipes implements ApplicationRunner {
    private final RecipeRepository recipeRepository;
    private final RecipeProducer recipeProducer;

    @Value("${spring.data.redis.run-sync-task}")
    private Boolean myEnvVariable;

    @Override
    public void run(ApplicationArguments args) {
        if (Boolean.FALSE.equals(myEnvVariable)) {
            return;
        }
        recipeRepository.findAll().forEach(recipe -> recipeProducer.send(recipe.getId()));
    }
}
