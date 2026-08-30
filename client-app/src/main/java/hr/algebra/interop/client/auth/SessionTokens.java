package hr.algebra.interop.client.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Component
@SessionScope
public class SessionTokens implements Serializable {

    private TokenPair tokens;

    public void store(TokenPair tokens) {
        this.tokens = tokens;
    }

    public TokenPair tokens() {
        return tokens;
    }

    public String accessToken() {
        return tokens == null ? null : tokens.accessToken();
    }

    public String refreshToken() {
        return tokens == null ? null : tokens.refreshToken();
    }

    public void clear() {
        this.tokens = null;
    }
}
