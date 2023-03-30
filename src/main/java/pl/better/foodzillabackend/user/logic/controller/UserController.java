package pl.better.foodzillabackend.user.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.user.logic.model.command.CreateUserCommand;
import pl.better.foodzillabackend.user.logic.model.dto.UserDto;
import pl.better.foodzillabackend.user.logic.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MutationMapping
    public UserDto createCustomer(@Argument @Valid CreateUserCommand customer) {
        return userService.createNewUserAndSaveInDb(customer);
    }
}
