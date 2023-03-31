package pl.better.foodzillabackend.auth.controller;

import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.token.Role;
import pl.better.foodzillabackend.auth.token.TokenGenerator;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TokenController {

    @Autowired
    private CustomerRepository applicationUserRepository;

    @Autowired
    private TokenGenerator tokenGenerator;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @MutationMapping
    public String getToken(@Argument String username, @Argument String password) {
        Optional<Customer> applicationUser = applicationUserRepository.findByUsername(username);

        if (applicationUser.isEmpty()) {
            throw new GraphQLException("Invalid credentials");
        }
        Customer user = applicationUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GraphQLException("Invalid credentials");
        }

        return tokenGenerator.build(user.getUsername(), Role.NORMAL);
    }


}
