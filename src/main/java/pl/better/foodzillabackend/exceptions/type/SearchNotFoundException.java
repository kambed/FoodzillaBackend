package pl.better.foodzillabackend.exceptions.type;

public class SearchNotFoundException extends NotFoundException {
    public SearchNotFoundException(String message) {
        super(message);
    }
}