package pl.better.foodzillabackend.recipe.logic.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecipeTemplate {
    private final RedisTemplate<Long, RecipeDto> redisTemplate;
    public void save(RecipeDto recipe) {
        redisTemplate.opsForValue().set(recipe.getId(), recipe);
    }

    public Optional<RecipeDto> getById(Long id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(id));
    }

    public List<RecipeDto> getRecipesByIds(List<Long> ids) {
        return redisTemplate.opsForValue().multiGet(ids);
    }

    public void deleteAll() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }
}
