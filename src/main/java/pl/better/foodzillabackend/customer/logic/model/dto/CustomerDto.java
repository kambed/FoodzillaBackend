package pl.better.foodzillabackend.customer.logic.model.dto;

import lombok.Builder;

@Builder
public record CustomerDto(String firstname,
                          String lastname,
                          String username,
                          String email) {}
