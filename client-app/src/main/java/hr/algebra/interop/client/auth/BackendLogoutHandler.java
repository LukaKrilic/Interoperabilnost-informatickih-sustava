package hr.algebra.interop.client.auth;

import hr.algebra.interop.client.config.BackendProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class BackendLogoutHandler implements LogoutHandler {

    private final RestClient rest;
    private final SessionTokens sessionTokens;

    public BackendLogoutHandler(BackendProperties backend, SessionTokens sessionTokens) {
        this.rest = RestClient.builder().baseUrl(backend.url()).build();
        this.sessionTokens = sessionTokens;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {

        String refreshToken = sessionTokens.refreshToken();
        if (refreshToken != null) {
            try {
                rest.post()
                        .uri("/api/auth/logout")
                        .body(Map.of("refreshToken", refreshToken))
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
            }
        }
        sessionTokens.clear();
    }
}
