package pl.better.foodzillabackend.mail.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordCommand(
        @NotNull
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_]).{5,}$",
                message = "Password must contain at least 8 characters, a capital letter and a special character")
        @Size(min = 8, max = 250)
        String newPassword,
        @NotNull
        @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
                message = "Email must match pattern")
        @Size(min = 5, max = 250)
        String email,
        @NotNull
        String resetPasswordToken
) {
}
