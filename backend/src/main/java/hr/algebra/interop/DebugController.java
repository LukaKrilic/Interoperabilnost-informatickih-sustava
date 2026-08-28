package hr.algebra.interop;

import hr.algebra.interop.asana.AsanaClient;
import hr.algebra.interop.asana.AsanaTag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DebugController {

    private final AsanaClient asana;

    public DebugController(AsanaClient asana) {
        this.asana = asana;
    }

    @GetMapping("/debug/asana-tags")
    public List<AsanaTag> tags() {
        return asana.listTags();
    }
}
