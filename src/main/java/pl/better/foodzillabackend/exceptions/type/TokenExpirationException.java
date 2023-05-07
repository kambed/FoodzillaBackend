package pl.better.foodzillabackend.exceptions.type;

public class TokenExpirationException extends ForbiddenException {
    public TokenExpirationException(String message) {
        super(message);
    }
}
