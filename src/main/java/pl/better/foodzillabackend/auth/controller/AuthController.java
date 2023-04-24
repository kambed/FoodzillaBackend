package pl.better.foodzillabackend.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.auth.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService tokenService;

    @MutationMapping
    public Token login(@Argument String username, @Argument String password) {
        return tokenService.generateToken(username, password);
    }
}
