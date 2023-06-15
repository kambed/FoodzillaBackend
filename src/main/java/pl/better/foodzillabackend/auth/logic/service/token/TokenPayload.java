package pl.better.foodzillabackend.auth.logic.service.token;

import pl.better.foodzillabackend.auth.logic.model.domain.Role;

public record TokenPayload(String username, Role role) {}
