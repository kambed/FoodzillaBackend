package pl.better.foodzillabackend.auth.token;

public class TokenPayload {
    private final String username;
    private final Role role;

    public TokenPayload(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
