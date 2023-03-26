package pl.better.foodzillabackend.user.logic.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping
public record CreateUserCommand(
        @NotNull
        @Size(min = 2, max = 100)
        String firstname,
        @NotNull
        @Size(min = 2, max = 100)
        String lastname,
        @NotNull
        @Size(min = 5, max = 100)
        String username,
        @NotNull
        @Size(min = 8, max = 250)
        String password
) {}