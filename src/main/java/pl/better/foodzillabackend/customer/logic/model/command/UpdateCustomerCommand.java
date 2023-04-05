package pl.better.foodzillabackend.customer.logic.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping
public record UpdateCustomerCommand(

        @NotNull
        @Size(min = 2, max = 100, message = "Firstname must be between 2 and 100 characters.")
        String firstname,
        @NotNull
        @Size(min = 2, max = 100, message = "Lastname must be between 2 and 100 characters.")
        String lastname,
        @NotNull
        @Size(min = 5, max = 100, message = "Username must be between 5 and 100 characters.")
        String username,
        @NotNull
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_]).{5,}$",
                message = "Password must contain at least 8 characters, a capital letter and a special character")
        @Size(min = 8, max = 250)
        String password
) {
}
