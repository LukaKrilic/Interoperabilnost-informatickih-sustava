package hr.algebra.interop.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String gid;

    @Column(nullable = false)
    private String name;

    private String color;

    private String notes;

    @Column(name = "workspace_gid")
    private String workspaceGid;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Tag() {
    }

    public Tag(String gid, String name, String color, String notes, String workspaceGid) {
        this.gid = gid;
        this.name = name;
        this.color = color;
        this.notes = notes;
        this.workspaceGid = workspaceGid;
    }

    public Long getId() { return id; }
    public String getGid() { return gid; }
    public void setGid(String gid) { this.gid = gid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getWorkspaceGid() { return workspaceGid; }
    public void setWorkspaceGid(String workspaceGid) { this.workspaceGid = workspaceGid; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
