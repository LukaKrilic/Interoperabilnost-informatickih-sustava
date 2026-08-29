package hr.algebra.interop.customapi;

import org.springframework.aop.support.AopUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiModeController {

    private final TagApiClient tags;

    public ApiModeController(TagApiClient tags) {
        this.tags = tags;
    }

    @GetMapping("/api/mode")
    public Map<String, String> mode() {
        return Map.of(
                "mode", tags.mode(),
                "implementation", AopUtils.getTargetClass(tags).getSimpleName(),
                "source", "custom".equals(tags.mode())
                        ? "PostgreSQL tablica tag"
                        : "https://app.asana.com/api/1.0/tags");
    }
}
