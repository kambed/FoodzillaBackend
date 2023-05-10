package pl.better.foodzillabackend.recipe.logic.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecipeTemplate {
    private final RedisTemplate<Long, RecipeDto> redisTemplate;
    public void save(RecipeDto recipe) {
        redisTemplate.opsForValue().set(recipe.id(), recipe);
    }

    public Optional<RecipeDto> getById(Long id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(id));
    }
}
