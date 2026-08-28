package hr.algebra.interop.imports;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XsdValidationService {

    private final Schema schema;

    public XsdValidationService() throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        this.schema = factory.newSchema(new ClassPathResource("schemas/tag.xsd").getURL());
    }

    public List<ValidationError> validate(byte[] xml) {
        List<ValidationError> errors = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    errors.add(toError(e));
                }

                @Override
                public void error(SAXParseException e) {
                    errors.add(toError(e));
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXParseException {
                    errors.add(toError(e));
                    throw e;
                }
            });
            validator.validate(new SAXSource(secureReader(),
                    new InputSource(new ByteArrayInputStream(xml))));
        } catch (SAXParseException e) {
            // already recorded by fatalError
        } catch (Exception e) {
            errors.add(new ValidationError("document", e.getMessage()));
        }
        return errors;
    }

    private static XMLReader secureReader() throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return spf.newSAXParser().getXMLReader();
    }

    private static ValidationError toError(SAXParseException e) {
        return new ValidationError(
                "line " + e.getLineNumber() + ", col " + e.getColumnNumber(),
                e.getMessage());
    }
}
