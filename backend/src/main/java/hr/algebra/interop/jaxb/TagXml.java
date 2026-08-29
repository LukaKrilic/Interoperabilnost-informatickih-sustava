package hr.algebra.interop.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TagXml {

    @XmlAttribute(name = "gid")
    private String gid;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "color")
    private String color;

    @XmlElement(name = "notes")
    private String notes;

    @XmlElement(name = "workspaceGid")
    private String workspaceGid;

    public String getGid() {
        return gid;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getNotes() {
        return notes;
    }

    public String getWorkspaceGid() {
        return workspaceGid;
    }
}
