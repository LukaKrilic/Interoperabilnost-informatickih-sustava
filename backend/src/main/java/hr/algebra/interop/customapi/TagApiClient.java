package hr.algebra.interop.customapi;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TagApiClient {

    String mode();

    List<TagDto> list(String term);

    TagDto get(String id);

    @PreAuthorize("hasRole('FULL')")
    TagDto create(TagRequest request);

    @PreAuthorize("hasRole('FULL')")
    TagDto update(String id, TagRequest request);

    @PreAuthorize("hasRole('FULL')")
    void delete(String id);
}
