package pl.better.foodzillabackend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.auth.service.token.JWTTokenUtils;
import pl.better.foodzillabackend.customer.logic.mapper.CustomerDtoMapper;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;
import pl.better.foodzillabackend.exceptions.type.InvalidCredentialsException;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final RecommendationService recommendationService;
    private final JWTTokenUtils tokenUtils;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDtoMapper mapper;
    private final Environment environment;

    public Token generateToken(String username, String password) {
        Optional<UserDetails> customer = Optional.ofNullable(customerService.loadUserByUsername(username));
        if (customer.isEmpty() || !passwordEncoder.matches(password, customer.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        recommendationService.recommend(username,
                Integer.parseInt(Objects.requireNonNull(environment.getProperty("NUM_OF_RECOMMENDATIONS"))));

        return tokenUtils.generateToken(username, mapper.apply((Customer) customer.get()));
    }
}
