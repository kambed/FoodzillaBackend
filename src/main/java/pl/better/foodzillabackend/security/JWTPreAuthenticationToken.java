package pl.better.foodzillabackend.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.Serial;

@Getter
public class JWTPreAuthenticationToken extends PreAuthenticatedAuthenticationToken {
    @Serial
    private static final long serialVersionUID = -5304730621727936850L;

    @Builder
    public JWTPreAuthenticationToken(JWTUserDetails principal, WebAuthenticationDetails details) {
        super(principal, null, principal.getAuthorities());
        super.setDetails(details);
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
