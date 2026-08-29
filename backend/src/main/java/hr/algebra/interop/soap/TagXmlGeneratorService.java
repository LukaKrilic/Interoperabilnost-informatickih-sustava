package hr.algebra.interop.soap;

import hr.algebra.interop.asana.AsanaClient;
import hr.algebra.interop.asana.AsanaTag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class TagXmlGeneratorService {

    public static final String NS = "http://algebra.hr/interop/tags";

    private final AsanaClient asana;
    private final Path target;

    public TagXmlGeneratorService(AsanaClient asana,
        @Value("${app.generated-xml}") String targetPath) {
        this.asana = asana;
        this.target = Path.of(targetPath);
    }

    public Path generate() {
        List<AsanaTag> tags = asana.listTags();
        write(build(tags));
        return target.toAbsolutePath();
    }

    private Document build(List<AsanaTag> tags) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();

            Element root = doc.createElementNS(NS, "tags");
            doc.appendChild(root);

            for (AsanaTag t : tags) {
                Element tag = doc.createElementNS(NS, "tag");
                if (t.gid() != null && !t.gid().isBlank()) {
                    tag.setAttribute("gid", t.gid());
                }
                append(doc, tag, "name", t.name());
                append(doc, tag, "color", t.color());
                append(doc, tag, "notes", t.notes());
                append(doc, tag, "workspaceGid",
                        t.workspace() == null ? null : t.workspace().gid());
                root.appendChild(tag);
            }
            return doc;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Ne mogu izgraditi XML dokument", e);
        }
    }

    private void append(Document doc, Element parent, String name, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        Element element = doc.createElementNS(NS, name);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private void write(Document doc) {
        try {
            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            try (OutputStream out = Files.newOutputStream(target)) {
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }
        } catch (IOException | TransformerException e) {
            throw new IllegalStateException("Ne mogu zapisati " + target, e);
        }
    }
}
