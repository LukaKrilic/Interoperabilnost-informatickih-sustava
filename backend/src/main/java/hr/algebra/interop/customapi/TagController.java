package hr.algebra.interop.customapi;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagApiClient tags;

    public TagController(TagApiClient tags) {
        this.tags = tags;
    }

    @GetMapping
    public List<TagDto> list(@RequestParam(required = false) String term) {
        return tags.list(term);
    }

    @GetMapping("/{id}")
    public TagDto one(@PathVariable String id) {
        return tags.get(id);
    }

    @PostMapping
    public ResponseEntity<TagDto> create(@Valid @RequestBody TagRequest request) {
        TagDto saved = tags.create(request);
        String identifier = saved.id() == null ? saved.gid() : String.valueOf(saved.id());

        return ResponseEntity
                .created(URI.create("/api/tags/" + identifier))
                .body(saved);
    }

    @PutMapping("/{id}")
    public TagDto update(@PathVariable String id, @Valid @RequestBody TagRequest request) {
        return tags.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        tags.delete(id);
        return ResponseEntity.noContent().build();
    }
}

