package pl.better.foodzillabackend.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenService {

    @Autowired
    private JWTTokenUtils tokenUtils;

    public String build(Object id, Object role) {
        return JWT.create()
                .withSubject(id.toString())
                .withClaim("role", role.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenUtils.getExpirationTime()))
                .sign(Algorithm.HMAC512(tokenUtils.getSecret().getBytes()));
    }
}
