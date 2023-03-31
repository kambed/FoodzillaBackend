package pl.better.foodzillabackend.auth.token;

public interface TokenGenerator {
    String build(Object id, Object role);
}