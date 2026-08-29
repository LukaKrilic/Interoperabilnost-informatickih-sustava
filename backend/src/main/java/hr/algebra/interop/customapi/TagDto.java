package hr.algebra.interop.customapi;

import hr.algebra.interop.domain.Tag;

import java.time.OffsetDateTime;

public record TagDto(Long id,
                     String gid,
                     String name,
                     String color,
                     String notes,
                     String workspaceGid,
                     OffsetDateTime createdAt) {

    public static TagDto from(Tag tag) {
        return new TagDto(tag.getId(),
                tag.getGid(),
                tag.getName(),
                tag.getColor(),
                tag.getNotes(),
                tag.getWorkspaceGid(),
                tag.getCreatedAt());
    }
}
