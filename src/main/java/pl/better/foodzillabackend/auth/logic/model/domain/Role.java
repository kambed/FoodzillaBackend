package pl.better.foodzillabackend.auth.logic.model.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    NORMAL,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
