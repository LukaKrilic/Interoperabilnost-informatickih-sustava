package hr.algebra.interop.client.backend;

public record TagView(String id,
                      String gid,
                      String name,
                      String color,
                      String notes,
                      String workspaceGid,
                      String createdAt) {

    public String identifier() {
        return id != null ? id : gid;
    }
}
