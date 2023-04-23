package pl.better.foodzillabackend.exceptions.type;

public class RecommendationErrorException extends RuntimeException{
    public RecommendationErrorException(String message) {
        super(message);
    }
}
