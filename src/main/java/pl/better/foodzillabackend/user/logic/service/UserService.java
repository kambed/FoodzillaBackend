package pl.better.foodzillabackend.user.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recipe.logic.model.dto.RecipeDto;
import pl.better.foodzillabackend.user.logic.exception.UserNotFoundException;
import pl.better.foodzillabackend.user.logic.mapper.UserDtoMapper;
import pl.better.foodzillabackend.user.logic.model.command.CreateUserCommand;
import pl.better.foodzillabackend.user.logic.model.dto.UserDto;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "User with id: %s not found";
    private final UserRepository userRepo;
    private final UserDtoMapper mapper;

    public UserDto getUserById(long id) {
        return userRepo.getUserById(id)
                .stream()
                .map(mapper)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(
                        USER_NOT_FOUND.formatted(id)
                ));
    }

    public RecipeDto createNewUserAndSaveInDb(CreateUserCommand user) {
        return null;
    }
}
