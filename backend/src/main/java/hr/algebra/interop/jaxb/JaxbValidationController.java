package hr.algebra.interop.jaxb;

import hr.algebra.interop.soap.TagXmlGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/jaxb")
public class JaxbValidationController {

    private final TagXmlGeneratorService generator;
    private final JaxbValidationService jaxb;

    public JaxbValidationController(TagXmlGeneratorService generator, JaxbValidationService jaxb) {
        this.generator = generator;
        this.jaxb = jaxb;
    }

    @GetMapping("/validate")
    public ResponseEntity<JaxbValidationResult> validate(
            @RequestParam(defaultValue = "true") boolean regenerate) {

        Path file = regenerate ? generator.generate() : generator.targetPath();
        JaxbValidationResult result = jaxb.validate(file);

        return result.valid()
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(result);
    }
}
