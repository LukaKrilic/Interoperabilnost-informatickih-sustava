package hr.algebra.interop.client.backend;

import hr.algebra.interop.client.auth.TokenRefreshInterceptor;
import hr.algebra.interop.client.config.BackendProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BackendImportClient {

    private final RestClient rest;
    private final BackendErrors errors;

    public BackendImportClient(BackendProperties backend, TokenRefreshInterceptor tokens, BackendErrors errors) {
        this.errors = errors;
        this.rest = RestClient.builder()
                .baseUrl(backend.url())
                .requestInterceptor(tokens)
                .build();
    }

    public ImportResult uvezi(String imeDatoteke, byte[] sadrzaj) {
        boolean xml = imeDatoteke != null && imeDatoteke.toLowerCase().endsWith(".xml");
        boolean json = imeDatoteke != null && imeDatoteke.toLowerCase().endsWith(".json");

        if (!xml && !json) {
            throw new BackendException(400,
                    "Podrzane su samo .xml i .json datoteke (poslano: " + imeDatoteke + ").");
        }

        return errors.guard(() -> rest.post()
                .uri(xml ? "/api/import/xml" : "/api/import/json")
                .contentType(xml ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON)
                .body(sadrzaj)
                .retrieve()
                .onStatus(status -> status.value() == 400, (request, response) -> { })
                .body(ImportResult.class));
    }

    public JaxbResult validirajJaxb(boolean regeneriraj) {
        return errors.guard(() -> rest.get()
                .uri("/api/jaxb/validate?regenerate={r}", regeneriraj)
                .retrieve()
                .onStatus(status -> status.value() == 400, (request, response) -> { })
                .body(JaxbResult.class));
    }
}
