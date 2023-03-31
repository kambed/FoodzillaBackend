package pl.better.foodzillabackend.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.auth.token.TokenGenerator;

import java.util.Date;

@Component
public class JWTTokenGenerator implements TokenGenerator {
    @Autowired
    private JWTTokenUtils tokenUtils;

    @Override
    public String build(Object id, Object role) {
        return JWT.create()
                .withSubject(id.toString())
                .withClaim("role", role.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenUtils.getExpirationTime()))
                .sign(Algorithm.HMAC512(tokenUtils.getSecret().getBytes()));
    }
}
