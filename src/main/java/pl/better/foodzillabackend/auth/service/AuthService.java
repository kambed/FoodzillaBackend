package pl.better.foodzillabackend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.auth.service.token.JWTTokenUtils;
import pl.better.foodzillabackend.auth.service.token.TokenPayload;
import pl.better.foodzillabackend.customer.logic.mapper.CustomerDtoMapper;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;
import pl.better.foodzillabackend.exceptions.type.InvalidCredentialsException;
import pl.better.foodzillabackend.exceptions.type.TokenExpirationException;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final RecommendationService recommendationService;
    private final JWTTokenUtils tokenUtils;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDtoMapper mapper;

    public Token generateToken(String username, String password) {
        Optional<UserDetails> customer = Optional.ofNullable(customerService.loadUserByUsername(username));
        if (customer.isEmpty() || !passwordEncoder.matches(password, customer.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        recommendationService.recommend(username);

        return tokenUtils.generateToken(username, mapper.apply((Customer) customer.get()));
    }

    public Token refreshToken(String refreshToken) {
        TokenPayload tokenPayload = tokenUtils.decodeRawToken(refreshToken);
        Optional<UserDetails> customer = Optional.ofNullable(customerService.loadUserByUsername(tokenPayload.username()));
        if (customer.isEmpty()) {
            throw new TokenExpirationException("Refresh token has expired");
        }

        return tokenUtils.generateToken(customer.get().getUsername(),
                mapper.apply((Customer) customer.get()),
                refreshToken);
    }
}
