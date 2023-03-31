package pl.better.foodzillabackend.auth.service;

public abstract class TokenUtils {

    private String headerString= "sdadsadasdsa";

    private String tokenPrefix = "Bearer";

    private String secret = "DZIALAJPLS";

    private long expirationTime = 10000000L;

    abstract public TokenPayload decodeToken(String authorizationHeader);

    public String getHeaderString() {
        return headerString;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getSecret() {
        return secret;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
