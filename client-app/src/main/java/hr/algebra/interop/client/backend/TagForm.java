package hr.algebra.interop.client.backend;

/**
 * Obicna klasa s getterima i setterima, ne record - Thymeleafov `th:field`
 * i Springov data binder trebaju JavaBean pristupnike.
 */
public class TagForm {

    private String gid;
    private String name;
    private String color;
    private String notes;
    private String workspaceGid;

    public TagForm() {
    }

    public static TagForm from(TagView tag) {
        TagForm form = new TagForm();
        form.gid = tag.gid();
        form.name = tag.name();
        form.color = tag.color();
        form.notes = tag.notes();
        form.workspaceGid = tag.workspaceGid();
        return form;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWorkspaceGid() {
        return workspaceGid;
    }

    public void setWorkspaceGid(String workspaceGid) {
        this.workspaceGid = workspaceGid;
    }
}
