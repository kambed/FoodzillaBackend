package pl.better.foodzillabackend.exceptions.type;

public class FilterInputException extends BadRequestException {
    public FilterInputException(String message) {
        super(message);
    }
}
