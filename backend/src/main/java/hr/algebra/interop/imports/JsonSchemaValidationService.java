package hr.algebra.interop.imports;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class JsonSchemaValidationService {

    private final JsonSchema schema;

    public JsonSchemaValidationService() throws IOException {
        JsonSchemaFactory factory =
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        this.schema = factory.getSchema(
                new ClassPathResource("schemas/tag-schema.json").getInputStream());
    }

    public List<ValidationError> validate(JsonNode document) {
        return schema.validate(document).stream()
                .map(m -> new ValidationError(
                        m.getInstanceLocation().toString(),
                        m.getMessage()))
                .sorted(Comparator.comparing(ValidationError::location))
                .toList();
    }
}
