package hr.algebra.interop.client.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
public class BackendErrors {

    private final ObjectMapper mapper;

    public BackendErrors(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T guard(Supplier<T> poziv) {
        try {
            return poziv.get();
        } catch (HttpStatusCodeException e) {
            throw new BackendException(e.getStatusCode().value(),
                    message(e.getStatusCode().value(), e.getResponseBodyAsString()));
        } catch (ResourceAccessException e) {
            throw new BackendException(503, "Backend nije dostupan. Radi li na portu 8080?");
        }
    }

    public String message(int status, String tijelo) {
        String detalji = sazetak(tijelo);

        return switch (status) {
            case 401 -> "Sesija je istekla. Prijavite se ponovno.";
            case 403 -> "Nemate ovlasti za ovu operaciju (potrebna je uloga FULL).";
            case 404 -> detalji.isBlank() ? "Trazeni zapis nije pronaden." : detalji;
            case 409 -> detalji.isBlank() ? "Zapis s tim gid-om vec postoji." : detalji;
            default -> detalji.isBlank() ? "Backend je vratio HTTP " + status : detalji;
        };
    }

    public List<ValidationErrorView> parse(String tijelo) {
        List<ValidationErrorView> greske = new ArrayList<>();
        if (tijelo == null || tijelo.isBlank()) {
            return greske;
        }
        try {
            JsonNode errors = mapper.readTree(tijelo).path("errors");
            if (errors.isArray()) {
                for (JsonNode error : errors) {
                    greske.add(new ValidationErrorView(
                            error.path("location").asText(),
                            error.path("message").asText()));
                }
            }
        } catch (Exception e) {
        }
        return greske;
    }

    public String pretty(String json) {
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mapper.readTree(json));
        } catch (Exception e) {
            return json;
        }
    }

    private String sazetak(String tijelo) {
        StringBuilder sb = new StringBuilder();
        for (ValidationErrorView greska : parse(tijelo)) {
            if (!sb.isEmpty()) {
                sb.append("; ");
            }
            sb.append(greska.location()).append(": ").append(greska.message());
        }
        return sb.toString();
    }
}
