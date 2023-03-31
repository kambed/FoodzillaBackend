package pl.better.foodzillabackend.exceptions.type;

public abstract class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
