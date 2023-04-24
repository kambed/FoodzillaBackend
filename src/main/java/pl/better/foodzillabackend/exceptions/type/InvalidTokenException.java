package pl.better.foodzillabackend.exceptions.type;

public class InvalidTokenException extends ForbiddenException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
