package hr.algebra.interop.client.auth;

public record TokenPair(String accessToken,
                        String refreshToken,
                        String username,
                        String role,
                        long accessMinutes) {
}
