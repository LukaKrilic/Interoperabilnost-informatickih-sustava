package hr.algebra.interop.graphqls;

import hr.algebra.interop.customapi.TagDto;
import hr.algebra.interop.service.TagService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TagGraphQlController {

    private final TagService service;

    public TagGraphQlController(TagService service) {
        this.service = service;
    }

    @QueryMapping
    public List<TagDto> tags(@Argument String term) {
        return service.search(term).stream().map(TagDto::from).toList();
    }

    @QueryMapping
    public TagDto tag(@Argument Long id) {
        return TagDto.from(service.findById(id));
    }

    @MutationMapping
    public TagDto createTag(@Argument TagInput input) {
        return TagDto.from(service.create(input.gid(), input.name(), input.color(),
                input.notes(), input.workspaceGid()));
    }

    @MutationMapping
    public TagDto updateTag(@Argument Long id, @Argument TagInput input) {
        return TagDto.from(service.update(id, input.gid(), input.name(), input.color(),
                input.notes(), input.workspaceGid()));
    }

    @MutationMapping
    public boolean deleteTag(@Argument Long id) {
        service.delete(id);
        return true;
    }
}