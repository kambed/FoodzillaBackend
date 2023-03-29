package pl.better.foodzillabackend.exceptions.type;

public class RecipeNotFoundException extends NotFoundException {
    public RecipeNotFoundException(String message) {
        super(message);
    }
}
