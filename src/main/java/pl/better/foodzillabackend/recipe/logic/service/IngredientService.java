package pl.better.foodzillabackend.recipe.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recipe.logic.mapper.IngredientDtoMapper;
import pl.better.foodzillabackend.recipe.logic.model.dto.IngredientDto;
import pl.better.foodzillabackend.recipe.logic.repository.IngredientRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientDtoMapper ingredientDtoMapper;

    public Set<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientDtoMapper)
                .collect(Collectors.toSet());
    }
}
