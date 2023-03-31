package pl.better.foodzillabackend.auth.controller;

import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.model.domain.Role;
import pl.better.foodzillabackend.auth.service.TokenService;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private CustomerRepository applicationUserRepository;
    @Autowired
    private TokenService tokenService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @MutationMapping
    public String login(@Argument String username, @Argument String password) {
        Optional<Customer> applicationUser = applicationUserRepository.findByUsername(username);

        if (applicationUser.isEmpty()) {
            throw new GraphQLException("Invalid credentials");
        }
        Customer user = applicationUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GraphQLException("Invalid credentials");
        }

        return tokenService.build(user.getUsername(), Role.NORMAL);
    }


}
