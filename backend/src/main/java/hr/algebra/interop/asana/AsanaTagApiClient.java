package hr.algebra.interop.asana;

import hr.algebra.interop.customapi.TagApiClient;
import hr.algebra.interop.customapi.TagDto;
import hr.algebra.interop.customapi.TagRequest;
import hr.algebra.interop.service.NotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Locale;

@Component
@ConditionalOnProperty(name = "tags.api.mode", havingValue = "public")
public class AsanaTagApiClient implements TagApiClient {

    private final AsanaClient asana;

    public AsanaTagApiClient(AsanaClient asana) {
        this.asana = asana;
    }

    @Override
    public String mode() {
        return "public";
    }

    @Override
    public List<TagDto> list(String term) {
        return asana.listTags().stream()
                .filter(tag -> matches(tag, term))
                .map(AsanaTagApiClient::toDto)
                .toList();
    }

    @Override
    public TagDto get(String id) {
        try {
            AsanaTag tag = asana.getTag(id);
            if (tag == null) {
                throw notFound(id);
            }
            return toDto(tag);
        } catch (HttpClientErrorException.NotFound e) {
            throw notFound(id);
        }
    }

    @Override
    public TagDto create(TagRequest request) {
        return toDto(asana.createTag(request.name(), request.color(), request.notes()));
    }

    @Override
    public TagDto update(String id, TagRequest request) {
        try {
            return toDto(asana.updateTag(id, request.name(), request.color(), request.notes()));
        } catch (HttpClientErrorException.NotFound e) {
            throw notFound(id);
        }
    }

    @Override
    public void delete(String id) {
        try {
            asana.deleteTag(id);
        } catch (HttpClientErrorException.NotFound e) {
            throw notFound(id);
        }
    }

    private static NotFoundException notFound(String id) {
        return new NotFoundException("Oznaka s gid " + id + " ne postoji u Asani");
    }

    private static boolean matches(AsanaTag tag, String term) {
        return term == null || term.isBlank()
                || tag.name() != null
                && tag.name().toLowerCase(Locale.ROOT).contains(term.toLowerCase(Locale.ROOT));
    }

    private static TagDto toDto(AsanaTag tag) {
        return new TagDto(null,
                tag.gid(),
                tag.name(),
                tag.color(),
                tag.notes(),
                tag.workspace() == null ? null : tag.workspace().gid(),
                null);
    }
}
