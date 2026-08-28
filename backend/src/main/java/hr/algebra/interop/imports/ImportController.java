package hr.algebra.interop.imports;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final XsdValidationService xsd;
    private final JsonSchemaValidationService json;
    private final TagImportService importService;

    public ImportController(XsdValidationService xsd,
                            JsonSchemaValidationService json,
                            TagImportService importService) {
        this.xsd = xsd;
        this.json = json;
        this.importService = importService;
    }

    @PostMapping(value = "/xml", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> importXml(@RequestBody byte[] body) throws Exception {
        List<ValidationError> errors = xsd.validate(body);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "errors", errors));
        }
        return ResponseEntity.ok(
                Map.of("valid", true, "imported", importService.saveFromXml(body)));
    }

    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> importJson(@RequestBody JsonNode body) {
        List<ValidationError> errors = json.validate(body);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "errors", errors));
        }
        return ResponseEntity.ok(
                Map.of("valid", true, "imported", importService.saveFromJson(body)));
    }
}
