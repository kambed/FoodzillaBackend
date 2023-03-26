package pl.better.foodzillabackend.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public class SecurityProperties {
    /**
     * Amount of hashing iterations, where formula is 2^passwordStrength iterations
     */
    private final int passwordStrength;
    /**
     * Secret used to generate and verify JWT tokens
     */
    private final String tokenSecret;
    /**
     * Name of the token issuer
     */
    private final String tokenIssuer = "whoiswho";
    /**
     * Duration after which a token will expire
     */
    private final Duration tokenExpiration = Duration.ofHours(4);
}
