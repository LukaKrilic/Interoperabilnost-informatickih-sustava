package hr.algebra.interop.imports;

import com.fasterxml.jackson.databind.JsonNode;
import hr.algebra.interop.domain.Tag;
import hr.algebra.interop.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagImportService {

    private static final String NS = "http://algebra.hr/interop/tags";

    private final TagRepository repository;

    public TagImportService(TagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public int saveFromXml(byte[] xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = dbf.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml));

        NodeList nodes = document.getElementsByTagNameNS(NS, "tag");
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            tags.add(new Tag(
                    e.hasAttribute("gid") ? e.getAttribute("gid") : null,
                    childText(e, "name"),
                    childText(e, "color"),
                    childText(e, "notes"),
                    childText(e, "workspaceGid")));
        }
        return repository.saveAll(tags).size();
    }

    @Transactional
    public int saveFromJson(JsonNode document) {
        List<Tag> tags = new ArrayList<>();
        for (JsonNode n : document.get("tags")) {
            tags.add(new Tag(
                    text(n, "gid"),
                    text(n, "name"),
                    text(n, "color"),
                    text(n, "notes"),
                    text(n, "workspaceGid")));
        }
        return repository.saveAll(tags).size();
    }

    private static String childText(Element parent, String localName) {
        NodeList list = parent.getElementsByTagNameNS(NS, localName);
        return list.getLength() == 0 ? null : list.item(0).getTextContent();
    }

    private static String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }
}
