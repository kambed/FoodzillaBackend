package pl.better.foodzillabackend.exceptions.type;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.text.MessageFormat;

@RequiredArgsConstructor
public class UserAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8369435603356630425L;
    private final String email;

    @Override
    public String getMessage() {
        return MessageFormat.format("A user already exists with email ''{0}''", email);
    }
}

