package hr.algebra.interop.graphqls;

public record TagInput(String gid,
                       String name,
                       String color,
                       String notes,
                       String workspaceGid) {
}