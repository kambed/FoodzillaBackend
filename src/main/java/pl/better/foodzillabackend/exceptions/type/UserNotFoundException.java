package pl.better.foodzillabackend.exceptions.type;

import pl.better.foodzillabackend.exceptions.type.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
