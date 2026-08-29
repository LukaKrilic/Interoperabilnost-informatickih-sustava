package hr.algebra.interop.soap;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Service
public class TagXPathSearchService {

    private static final String NS = TagXmlGeneratorService.NS;

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZČĆĐŠŽ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyzčćđšž";

    private static final String ALL_TAGS = "/t:tags/t:tag";

    private static final String MATCHING_TAGS =
            "/t:tags/t:tag[contains(translate(t:name, '" + UPPER + "', '" + LOWER + "'), $term)]";

    public SearchResult search(Path file, String rawTerm) {
        Document document = parse(file);
        String term = rawTerm == null ? "" : rawTerm.trim().toLowerCase(Locale.ROOT);

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new TagsNamespaceContext());
        xpath.setXPathVariableResolver(name ->
                "term".equals(name.getLocalPart()) ? term : null);

        try {
            NodeList all = (NodeList) xpath.evaluate(ALL_TAGS, document, XPathConstants.NODESET);
            NodeList matches = (NodeList) xpath.evaluate(MATCHING_TAGS, document, XPathConstants.NODESET);

            List<FoundTag> found = new ArrayList<>(matches.getLength());
            for (int i = 0; i < matches.getLength(); i++) {
                Node node = matches.item(i);
                found.add(new FoundTag(
                        blankToNull(xpath.evaluate("@gid", node)),
                        xpath.evaluate("t:name", node),
                        blankToNull(xpath.evaluate("t:color", node)),
                        blankToNull(xpath.evaluate("t:notes", node)),
                        blankToNull(xpath.evaluate("t:workspaceGid", node))));
            }
            return new SearchResult(all.getLength(), found);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Neispravan XPath izraz", e);
        }
    }

    private Document parse(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return dbf.newDocumentBuilder().parse(in);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new IllegalStateException("Ne mogu pročitati " + file, e);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static final class TagsNamespaceContext implements NamespaceContext {
        @Override
        public String getNamespaceURI(String prefix) {
            return "t".equals(prefix) ? NS : XMLConstants.NULL_NS_URI;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return NS.equals(namespaceURI) ? "t" : null;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return getPrefix(namespaceURI) == null
                    ? Collections.emptyIterator()
                    : List.of("t").iterator();
        }
    }
}
