package hr.algebra.interop.repository;

import hr.algebra.interop.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByGid(String gid);

    List<Tag> findByNameContainingIgnoreCase(String term);
}
