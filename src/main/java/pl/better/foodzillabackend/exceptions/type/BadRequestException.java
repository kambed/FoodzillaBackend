package pl.better.foodzillabackend.exceptions.type;

public abstract class BadRequestException extends RuntimeException {
    protected BadRequestException(String message) {
        super(message);
    }
}
