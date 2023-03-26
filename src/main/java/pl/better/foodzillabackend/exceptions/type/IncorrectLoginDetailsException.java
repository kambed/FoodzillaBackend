package pl.better.foodzillabackend.exceptions.type;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.text.MessageFormat;

@RequiredArgsConstructor
public class IncorrectLoginDetailsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4129146858129498534L;
    private final String username;

    @Override
    public String getMessage() {
        return MessageFormat.format("Email or password didn''t match for ''{0}''", username);
    }
}