package hr.algebra.interop.client.backend;

public record TagView(String id,
                      String gid,
                      String name,
                      String color,
                      String notes,
                      String workspaceGid,
                      String createdAt) {

    /**
     * U custom nacinu rada backend vraca numericki id, u public nacinu Asanin gid.
     * Tocno jedno od to dvoje je popunjeno, pa poveznice koriste ono sto postoji.
     */
    public String identifier() {
        return id != null ? id : gid;
    }
}
