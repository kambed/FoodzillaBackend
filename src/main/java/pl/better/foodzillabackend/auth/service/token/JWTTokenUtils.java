package pl.better.foodzillabackend.auth.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.auth.model.domain.Role;
import pl.better.foodzillabackend.auth.model.domain.Token;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;

import java.util.Date;

@Component
public class JWTTokenUtils extends TokenUtils {

    public TokenPayload decodeToken(String authorizationHeader) {
        DecodedJWT decodedToken = JWT
                .require(Algorithm.HMAC512(getSecret().getBytes()))
                .build()
                .verify(authorizationHeader.replace(getTokenPrefix(), ""));

        return new TokenPayload(decodedToken.getSubject(), decodedToken.getClaim("role")
                .as(Role.class));
    }

    public Token generateToken(String username, CustomerDto customerDto) {
        return Token.builder()
                .token(JWT.create()
                        .withSubject(username)
                        .withClaim("role", Role.NORMAL.toString())
                        .withExpiresAt(new Date(System.currentTimeMillis() + getExpirationTime()))
                        .sign(Algorithm.HMAC512(getSecret().getBytes())))
                .customer(customerDto)
                .build();
    }
}
