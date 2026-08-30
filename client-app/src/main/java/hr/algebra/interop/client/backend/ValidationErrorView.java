package hr.algebra.interop.client.backend;

/**
 * Isti {location, message} oblik koji backend vraca za zahtjeve 1, 3 i 5.
 * Jedan tip ovdje znaci jedan Thymeleaf fragment za sve stranice s greskama.
 */
public record ValidationErrorView(String location, String message) {
}
