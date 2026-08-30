package hr.algebra.interop.client.backend;

import hr.algebra.interop.client.auth.TokenRefreshInterceptor;
import hr.algebra.interop.client.config.BackendProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BackendTagClient {

    private final RestClient rest;
    private final BackendErrors errors;

    public BackendTagClient(BackendProperties backend, TokenRefreshInterceptor tokens, BackendErrors errors) {
        this.errors = errors;
        this.rest = RestClient.builder()
                .baseUrl(backend.url())
                .requestInterceptor(tokens)
                .build();
    }

    public Map<String, String> mode() {
        return errors.guard(() -> rest.get()
                .uri("/api/mode")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {}));
    }

    public List<TagView> list(String term) {
        String uri = UriComponentsBuilder.fromPath("/api/tags")
                .queryParamIfPresent("term", Optional.ofNullable(
                        term == null || term.isBlank() ? null : term))
                .build()
                .toUriString();

        return errors.guard(() -> rest.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<TagView>>() {}));
    }

    public TagView get(String id) {
        return errors.guard(() -> rest.get()
                .uri("/api/tags/{id}", id)
                .retrieve()
                .body(TagView.class));
    }

    public TagView create(TagForm form) {
        return errors.guard(() -> rest.post()
                .uri("/api/tags")
                .body(form)
                .retrieve()
                .body(TagView.class));
    }

    public TagView update(String id, TagForm form) {
        return errors.guard(() -> rest.put()
                .uri("/api/tags/{id}", id)
                .body(form)
                .retrieve()
                .body(TagView.class));
    }

    public void delete(String id) {
        errors.guard(() -> rest.delete()
                .uri("/api/tags/{id}", id)
                .retrieve()
                .toBodilessEntity());
    }

    public String graphql(String query) {
        String raw = errors.guard(() -> rest.post()
                .uri("/graphql")
                .body(Map.of("query", query))
                .retrieve()
                .body(String.class));
        return errors.pretty(raw);
    }
}
