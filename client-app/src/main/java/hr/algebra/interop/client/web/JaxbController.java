package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.BackendImportClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/jaxb")
public class JaxbController {

    private final BackendImportClient backend;

    public JaxbController(BackendImportClient backend) {
        this.backend = backend;
    }

    @GetMapping
    public String stranica() {
        return "jaxb";
    }

    @PostMapping
    public String validiraj(@RequestParam(defaultValue = "false") boolean regeneriraj, Model model) {
        model.addAttribute("regeneriraj", regeneriraj);
        try {
            model.addAttribute("rezultat", backend.validirajJaxb(regeneriraj));
        } catch (BackendException e) {
            model.addAttribute("greska", e.getMessage());
        }
        return "jaxb";
    }
}
