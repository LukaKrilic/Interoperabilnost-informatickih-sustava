package hr.algebra.interop.auth;

public record TokenPair(String accessToken,
                        String refreshToken,
                        String username,
                        String role,
                        long accessMinutes) {
}
