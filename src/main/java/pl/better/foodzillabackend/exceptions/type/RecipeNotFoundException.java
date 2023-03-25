package pl.better.foodzillabackend.exceptions.type;

import pl.better.foodzillabackend.exceptions.type.NotFoundException;

public class RecipeNotFoundException extends NotFoundException {
    public RecipeNotFoundException(String message) {
        super(message);
    }
}
