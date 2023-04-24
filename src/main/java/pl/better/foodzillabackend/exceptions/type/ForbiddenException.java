package pl.better.foodzillabackend.exceptions.type;

public abstract class ForbiddenException extends RuntimeException {
    protected ForbiddenException(String message) {
        super(message);
    }
}
