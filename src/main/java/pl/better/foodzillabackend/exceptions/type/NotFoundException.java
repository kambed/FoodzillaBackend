package pl.better.foodzillabackend.exceptions.type;

public abstract class NotFoundException extends RuntimeException {
    protected NotFoundException(String message) {
        super(message);
    }
}
