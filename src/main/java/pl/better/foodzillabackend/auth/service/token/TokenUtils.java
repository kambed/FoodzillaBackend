package pl.better.foodzillabackend.auth.service.token;

import org.springframework.beans.factory.annotation.Value;

public abstract class TokenUtils {

    @Value("${spring.security.jwt.header-name}")
    private String headerString;

    @Value("${spring.security.jwt.prefix}")
    private String tokenPrefix;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private long expirationTime;

    abstract public TokenPayload decodeToken(String authorizationHeader);

    public String getHeaderString() {
        return headerString;
    }

    public String getTokenPrefix() {
        return tokenPrefix + " ";
    }

    public String getSecret() {
        return secret;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}