package pl.better.foodzillabackend.exceptions.type;

public abstract class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
