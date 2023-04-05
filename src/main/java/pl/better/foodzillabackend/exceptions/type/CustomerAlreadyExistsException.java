package pl.better.foodzillabackend.exceptions.type;

public class CustomerAlreadyExistsException extends BadRequestException {
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}

