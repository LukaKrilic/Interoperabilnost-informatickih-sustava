package hr.algebra.interop.client.auth;

import hr.algebra.interop.client.config.BackendProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Map;

/**
 * Dodaje Bearer token na svaki odlazni poziv i, ako backend odgovori 401,
 * jednom osvjezi par tokena i ponovi izvorni zahtjev.
 */
@Component
public class TokenRefreshInterceptor implements ClientHttpRequestInterceptor {

    private final SessionTokens tokens;

    /** Namjerno BEZ ovog interceptora - inace bi 401 na /api/auth/refresh usao u rekurziju. */
    private final RestClient refreshClient;

    public TokenRefreshInterceptor(SessionTokens tokens, BackendProperties backend) {
        this.tokens = tokens;
        this.refreshClient = RestClient.builder().baseUrl(backend.url()).build();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        String token = tokens.accessToken();
        if (token != null) {
            request.getHeaders().setBearerAuth(token);
        }

        ClientHttpResponse response = execution.execute(request, body);

        boolean istekao = response.getStatusCode().value() == 401 && tokens.refreshToken() != null;
        if (!istekao || !osvjezi()) {
            return response;
        }

        // Tocno jedan ponovni pokusaj. Prvi odgovor se mora zatvoriti prije ponavljanja.
        response.close();
        request.getHeaders().setBearerAuth(tokens.accessToken());
        return execution.execute(request, body);
    }

    private boolean osvjezi() {
        String refresh = tokens.refreshToken();
        try {
            TokenPair novi = refreshClient.post()
                    .uri("/api/auth/refresh")
                    .body(Map.of("refreshToken", refresh))
                    .retrieve()
                    .body(TokenPair.class);

            if (novi == null || novi.accessToken() == null) {
                return false;
            }
            tokens.store(novi);
            return true;
        } catch (Exception e) {
            // refresh token je istekao ili opozvan - korisnik se mora ponovno prijaviti
            tokens.clear();
            return false;
        }
    }
}
