package hr.algebra.interop.auth;

import hr.algebra.interop.domain.AppUser;
import hr.algebra.interop.domain.RefreshToken;
import hr.algebra.interop.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long refreshDays;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${jwt.refresh-days}") long refreshDays) {
        this.repository = repository;
        this.refreshDays = refreshDays;
    }

    @Transactional
    public RefreshToken issue(AppUser user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        return repository.save(new RefreshToken(user, value,
                OffsetDateTime.now().plusDays(refreshDays)));
    }

    @Transactional
    public RefreshToken rotate(String presented) {
        RefreshToken current = repository.findByToken(presented)
                .orElseThrow(() -> new AuthException("Refresh token nije prepoznat"));

        if (current.isRevoked()) {
            throw new AuthException("Refresh token je vec iskoristen ili opozvan");
        }
        if (current.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new AuthException("Refresh token je istekao");
        }

        current.setRevoked(true);
        return issue(current.getUser());
    }

    @Transactional
    public void revoke(String presented) {
        repository.findByToken(presented).ifPresent(token -> token.setRevoked(true));
    }
}
