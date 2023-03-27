package pl.better.foodzillabackend.user.logic.model.dto;

import lombok.Builder;

@Builder
public record UserDto(String firstname,
                      String lastname,
                      String username) {}
