package pl.better.foodzillabackend.exceptions.type;

import java.io.Serial;

public class InvalidTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 158136221282852553L;

    @Override
    public String getMessage() {
        return "Token is invalid or expired";
    }
}
