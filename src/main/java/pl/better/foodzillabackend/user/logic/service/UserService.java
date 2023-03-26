package pl.better.foodzillabackend.user.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.exceptions.type.UserAlreadyExistsException;
import pl.better.foodzillabackend.exceptions.type.UserNotFoundException;
import pl.better.foodzillabackend.user.logic.mapper.UserDtoMapper;
import pl.better.foodzillabackend.user.logic.model.command.CreateUserCommand;
import pl.better.foodzillabackend.user.logic.model.domain.User;
import pl.better.foodzillabackend.user.logic.model.dto.UserDto;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "User with id: %s not found";
    private static final String USER_ALREADY_EXIST = "User with username: %s already exists";
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

    @Transactional
    public UserDto createNewUserAndSaveInDb(CreateUserCommand command) {
        if (!exists(command)) {
            User user = User.builder()
                    .firstname(command.firstname())
                    .lastname(command.lastname())
                    .username(command.username())
                    //TODO: password encryption, related to security configuration
                    .password(command.password())
                    .build();

            userRepo.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new UserAlreadyExistsException(USER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    private boolean exists(CreateUserCommand command) {
        return userRepo.existsByUsername(command.username());
    }
}
