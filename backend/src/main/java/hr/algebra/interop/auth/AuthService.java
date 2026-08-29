package hr.algebra.interop.auth;

import hr.algebra.interop.domain.AppUser;
import hr.algebra.interop.domain.RefreshToken;
import hr.algebra.interop.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final RefreshTokenService refreshTokens;

    public AuthService(AppUserRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtService jwt,
                       RefreshTokenService refreshTokens) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.refreshTokens = refreshTokens;
    }

    @Transactional
    public TokenPair login(String username, String password) {
        AppUser user = users.findByUsername(username)
                .orElseThrow(() -> new AuthException("Neispravno korisnicko ime ili lozinka"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthException("Neispravno korisnicko ime ili lozinka");
        }

        return new TokenPair(
                jwt.createAccessToken(user.getUsername(), user.getRole()),
                refreshTokens.issue(user).getToken(),
                user.getUsername(),
                user.getRole(),
                jwt.accessMinutes());
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        RefreshToken rotated = refreshTokens.rotate(refreshToken);
        AppUser user = rotated.getUser();

        return new TokenPair(
                jwt.createAccessToken(user.getUsername(), user.getRole()),
                rotated.getToken(),
                user.getUsername(),
                user.getRole(),
                jwt.accessMinutes());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokens.revoke(refreshToken);
    }
}
