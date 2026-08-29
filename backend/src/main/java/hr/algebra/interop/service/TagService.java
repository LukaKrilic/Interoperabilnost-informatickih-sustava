package hr.algebra.interop.service;

import hr.algebra.interop.domain.Tag;
import hr.algebra.interop.repository.TagRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Tag> search(String term) {
        return term == null || term.isBlank()
                ? repository.findAll()
                : repository.findByNameContainingIgnoreCase(term);
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Oznaka s id " + id + " ne postoji"));
    }

    @PreAuthorize("hasRole('FULL')")
    @Transactional
    public Tag create(String gid, String name, String color, String notes, String workspaceGid) {
        return repository.save(new Tag(gid, name, color, notes, workspaceGid));
    }

    @PreAuthorize("hasRole('FULL')")
    @Transactional
    public Tag update(Long id, String gid, String name, String color, String notes, String workspaceGid) {
        Tag tag = findById(id);
        tag.setGid(gid);
        tag.setName(name);
        tag.setColor(color);
        tag.setNotes(notes);
        tag.setWorkspaceGid(workspaceGid);
        return tag;
    }

    @PreAuthorize("hasRole('FULL')")
    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
