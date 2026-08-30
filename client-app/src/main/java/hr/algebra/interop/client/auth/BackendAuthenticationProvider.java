package hr.algebra.interop.client.auth;

import hr.algebra.interop.client.config.BackendProperties;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class BackendAuthenticationProvider implements AuthenticationProvider {

    private final RestClient rest;
    private final SessionTokens sessionTokens;

    public BackendAuthenticationProvider(BackendProperties backend, SessionTokens sessionTokens) {
        this.rest = RestClient.builder().baseUrl(backend.url()).build();
        this.sessionTokens = sessionTokens;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        TokenPair pair;
        try {
            pair = rest.post()
                    .uri("/api/auth/login")
                    .body(Map.of("username", username, "password", password))
                    .retrieve()
                    .body(TokenPair.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadCredentialsException("Neispravno korisnicko ime ili lozinka");
        } catch (ResourceAccessException e) {
            throw new AuthenticationServiceException("Backend nije dostupan na " + rest, e);
        }

        if (pair == null || pair.accessToken() == null) {
            throw new BadCredentialsException("Backend nije vratio token");
        }

        sessionTokens.store(pair);

        return new UsernamePasswordAuthenticationToken(
                pair.username(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + pair.role())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
