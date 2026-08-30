package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.BackendTagClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/graphql")
public class GraphQlController {

    private static final String PRIMJER = "{ tags { id name color } }";

    private final BackendTagClient backend;

    public GraphQlController(BackendTagClient backend) {
        this.backend = backend;
    }

    @GetMapping
    public String konzola(Model model) {
        model.addAttribute("upit", PRIMJER);
        return "graphql";
    }

    @PostMapping
    public String izvrsi(@RequestParam String upit, Model model) {
        model.addAttribute("upit", upit);
        try {
            model.addAttribute("odgovor", backend.graphql(upit));
        } catch (BackendException e) {
            model.addAttribute("greska", e.getMessage());
        }
        return "graphql";
    }
}
