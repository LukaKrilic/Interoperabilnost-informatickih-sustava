package hr.algebra.interop.jaxb;

import hr.algebra.interop.imports.ValidationError;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventLocator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class JaxbValidationService {

    private final JAXBContext context;
    private final Schema schema;

    public JaxbValidationService() throws Exception {
        this.context = JAXBContext.newInstance(TagsDocument.class);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        this.schema = factory.newSchema(new ClassPathResource("schemas/tag.xsd").getURL());
    }

    public JaxbValidationResult validate(Path file) {
        List<ValidationError> errors = new ArrayList<>();
        int tagCount = 0;

        try (InputStream in = Files.newInputStream(file)) {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(event -> {
                errors.add(toError(event));
                return true;
            });

            Object root = unmarshaller.unmarshal(
                    new SAXSource(secureReader(), new InputSource(in)));

            if (root instanceof TagsDocument document) {
                tagCount = document.getTags().size();
            }
        } catch (NoSuchFileException e) {
            errors.add(new ValidationError("file", "Datoteka ne postoji: " + file));
        } catch (Exception e) {
            errors.add(new ValidationError("document", e.getMessage()));
        }

        return new JaxbValidationResult(errors.isEmpty(), file.toString(), tagCount, errors);
    }

    private static XMLReader secureReader() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return spf.newSAXParser().getXMLReader();
    }

    private static ValidationError toError(ValidationEvent event) {
        ValidationEventLocator locator = event.getLocator();
        String location = locator == null
                ? "document"
                : "line " + locator.getLineNumber() + ", col " + locator.getColumnNumber();
        return new ValidationError(location, event.getMessage());
    }
}
