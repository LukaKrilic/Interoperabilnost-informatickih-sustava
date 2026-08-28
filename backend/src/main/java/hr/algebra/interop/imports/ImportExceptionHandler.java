package hr.algebra.interop.imports;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ImportExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> unreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "errors", List.of(new ValidationError("document",
                        "Tijelo zahtjeva nije ispravan JSON: " + e.getMostSpecificCause().getMessage()))));
    }
}
