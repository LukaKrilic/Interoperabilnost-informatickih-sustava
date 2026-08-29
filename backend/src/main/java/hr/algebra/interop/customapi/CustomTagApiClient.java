package hr.algebra.interop.customapi;

import hr.algebra.interop.service.NotFoundException;
import hr.algebra.interop.service.TagService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "tags.api.mode", havingValue = "custom")
public class CustomTagApiClient implements TagApiClient {

    private final TagService service;

    public CustomTagApiClient(TagService service) {
        this.service = service;
    }

    @Override
    public String mode() {
        return "custom";
    }

    @Override
    public List<TagDto> list(String term) {
        return service.search(term).stream().map(TagDto::from).toList();
    }

    @Override
    public TagDto get(String id) {
        return TagDto.from(service.findById(asLong(id)));
    }

    @Override
    public TagDto create(TagRequest request) {
        return TagDto.from(service.create(request.gid(), request.name(), request.color(),
                request.notes(), request.workspaceGid()));
    }

    @Override
    public TagDto update(String id, TagRequest request) {
        return TagDto.from(service.update(asLong(id), request.gid(), request.name(),
                request.color(), request.notes(), request.workspaceGid()));
    }

    @Override
    public void delete(String id) {
        service.delete(asLong(id));
    }

    private static Long asLong(String id) {
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Oznaka s id " + id + " ne postoji");
        }
    }
}
