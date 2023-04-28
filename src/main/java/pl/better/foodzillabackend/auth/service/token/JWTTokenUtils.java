package pl.better.foodzillabackend.auth.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.auth.model.domain.Role;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.exceptions.type.TokenExpirationException;

import java.util.Date;

@Component
public class JWTTokenUtils extends TokenUtils {

    public TokenPayload decodeToken(String authorizationHeader) {
        try {
            DecodedJWT decodedToken = JWT
                    .require(Algorithm.HMAC512(getSecret().getBytes()))
                    .build()
                    .verify(authorizationHeader.replace(getTokenPrefix(), ""));

            return new TokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role")
                    .as(Role.class));
        } catch (TokenExpiredException e) {
            throw new TokenExpirationException(e.getMessage());
        }
    }

    public TokenPayload decodeRawToken(String token) {
        try {
            DecodedJWT decodedToken = JWT
                    .require(Algorithm.HMAC512(getSecret().getBytes()))
                    .build()
                    .verify(token);

            return new TokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role")
                    .as(Role.class));
        } catch (TokenExpiredException e) {
            throw new TokenExpirationException(e.getMessage());
        }
    }

    public Token generateToken(String username, CustomerDto customerDto) {
        return Token.builder()
                .token(createToken(username,
                        new Date(System.currentTimeMillis() + getExpirationTime())))
                .refreshToken(createToken(username,
                        new Date(System.currentTimeMillis() + getRefreshExpirationTime())))
                .customer(customerDto)
                .build();
    }

    public Token generateToken(String username, CustomerDto customerDto, String refreshToken) {
        return Token.builder()
                .token(createToken(username,
                        new Date(System.currentTimeMillis() + getExpirationTime())))
                .refreshToken(refreshToken)
                .customer(customerDto)
                .build();
    }

    private String createToken(String username, Date date) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", Role.NORMAL.toString())
                .withExpiresAt(date)
                .sign(Algorithm.HMAC512(getSecret().getBytes()));
    }
}
