package pl.better.foodzillabackend.user.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.user.logic.exception.UserNotFoundException;
import pl.better.foodzillabackend.user.logic.mapper.UserDtoMapper;
import pl.better.foodzillabackend.user.logic.model.command.CreateUserCommand;
import pl.better.foodzillabackend.user.logic.model.domain.User;
import pl.better.foodzillabackend.user.logic.model.dto.UserDto;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "User with id: %s not found";
    private final UserRepository userRepo;
    private final UserDtoMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserById(long id) {
        return userRepo.getUserById(id)
                .stream()
                .map(mapper)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(
                        USER_NOT_FOUND.formatted(id)
                ));
    }

    public UserDto createNewUserAndSaveInDb(CreateUserCommand command) {
        User user = User.builder()
                .firstname(command.firstname())
                .lastname(command.lastname())
                .username(command.username())
                .password(passwordEncoder.encode(command.password()))
                .build();

        userRepo.saveAndFlush(user);
        return mapper.apply(user);
    }
}
