package pl.better.foodzillabackend.exceptions.type;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
