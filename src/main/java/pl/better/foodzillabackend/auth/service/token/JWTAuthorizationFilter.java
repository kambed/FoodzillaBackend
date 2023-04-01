package pl.better.foodzillabackend.auth.service.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.exceptions.type.InvalidTokenException;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final TokenUtils tokenUtils;

    public JWTAuthorizationFilter(AuthenticationManager authManager, TokenUtils tokenUtils) {
        super(authManager);
        this.tokenUtils = tokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(tokenUtils.getHeaderString());
        if (header == null || !header.startsWith(tokenUtils.getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(tokenUtils.getHeaderString());
        if (token == null) {
            return null;
        }

        TokenPayload tokenPayload = tokenUtils.decodeToken(token);
        if (tokenPayload.username() == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(tokenPayload.username(),
                null,
                Collections.singletonList(tokenPayload.role()));
    }
}
