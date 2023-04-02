package pl.better.foodzillabackend.auth.service.token;

import pl.better.foodzillabackend.auth.model.domain.Role;

public record TokenPayload(String username, Role role) {}
