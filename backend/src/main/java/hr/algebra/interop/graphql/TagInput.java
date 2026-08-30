package hr.algebra.interop.graphql;

public record TagInput(String gid,
                       String name,
                       String color,
                       String notes,
                       String workspaceGid) {
}