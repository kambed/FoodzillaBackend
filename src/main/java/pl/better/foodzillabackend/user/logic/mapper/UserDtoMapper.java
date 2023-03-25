package pl.better.foodzillabackend.user.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.user.logic.model.domain.User;
import pl.better.foodzillabackend.user.logic.model.dto.UserDto;

import java.util.function.Function;

@Component
public class UserDtoMapper implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user) {
        return UserDto.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .build();
    }
}
