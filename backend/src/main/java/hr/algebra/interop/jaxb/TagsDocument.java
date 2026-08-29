package hr.algebra.interop.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "tags")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagsDocument {

    @XmlElement(name = "tag")
    private List<TagXml> tags = new ArrayList<>();

    public List<TagXml> getTags() {
        return tags;
    }
}
