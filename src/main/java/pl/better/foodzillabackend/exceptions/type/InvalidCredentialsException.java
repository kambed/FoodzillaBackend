package pl.better.foodzillabackend.exceptions.type;

public class InvalidCredentialsException extends ForbiddenException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
