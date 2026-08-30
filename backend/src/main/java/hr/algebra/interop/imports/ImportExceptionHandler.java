package hr.algebra.interop.imports;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice(assignableTypes = ImportController.class)
public class ImportExceptionHandler {

    /** Postgres u poruci navodi koji je kljuc pao: Key (gid)=(1200000000000001) already exists. */
    private static final Pattern KLJUC = Pattern.compile("Key \\((\\w+)\\)=\\(([^)]*)\\)");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> unreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "errors", List.of(new ValidationError("document",
                        "Tijelo zahtjeva nije ispravan JSON: " + e.getMostSpecificCause().getMessage()))));
    }

    /**
     * Uvoz datoteke koja sadrzi gid koji je vec u bazi. Zahtjev je ispravan i validacija je prosla,
     * ali se sudara s postojecim stanjem - to je 409, ne 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "valid", false,
                "errors", List.of(new ValidationError("gid", poruka(e)))));
    }

    private static String poruka(DataIntegrityViolationException e) {
        String uzrok = e.getMostSpecificCause().getMessage();
        Matcher m = uzrok == null ? null : KLJUC.matcher(uzrok);

        if (m != null && m.find()) {
            return "Oznaka s vrijednoscu " + m.group(1) + " = " + m.group(2)
                    + " vec postoji u bazi. Nijedan zapis iz datoteke nije spremljen.";
        }
        return "Datoteka se sudara s postojecim zapisom u bazi. "
                + "Nijedan zapis iz datoteke nije spremljen.";
    }
}
