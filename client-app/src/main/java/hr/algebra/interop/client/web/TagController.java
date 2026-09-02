package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.BackendTagClient;
import hr.algebra.interop.client.backend.TagForm;
import hr.algebra.interop.client.backend.TagView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oznake")
public class TagController {

    static final List<String> BOJE = List.of("", "red", "orange", "yellow-orange", "yellow",
            "yellow-green", "green", "blue-green", "aqua", "blue", "indigo", "purple",
            "magenta", "hot-pink", "pink", "cool-gray", "none");

    private final BackendTagClient backend;

    public TagController(BackendTagClient backend) {
        this.backend = backend;
    }

    @GetMapping
    public String popis(@RequestParam(required = false) String term, Model model) {
        model.addAttribute("term", term);
        try {
            model.addAttribute("oznake", backend.list(term));
            model.addAttribute("nacin", backend.mode());
        } catch (BackendException e) {
            model.addAttribute("oznake", List.of());
            model.addAttribute("greska", e.getMessage());
        }
        return "tags";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("oznaka", new TagForm());
        model.addAttribute("boje", BOJE);
        model.addAttribute("naslov", "Nova oznaka");
        return "tag-form";
    }

    @GetMapping("/{id}/uredi")
    public String uredi(@PathVariable String id, Model model, RedirectAttributes redirect) {
        try {
            TagView tag = backend.get(id);
            model.addAttribute("oznaka", TagForm.from(tag));
            model.addAttribute("id", id);
            model.addAttribute("boje", BOJE);
            model.addAttribute("naslov", "Uredivanje oznake");
            return "tag-form";
        } catch (BackendException e) {
            redirect.addFlashAttribute("greska", e.getMessage());
            return "redirect:/oznake";
        }
    }

    @PostMapping("/spremi")
    public String spremi(@RequestParam(required = false) String id,
                         @ModelAttribute TagForm oznaka,
                         RedirectAttributes redirect) {
        try {
            if (id == null || id.isBlank()) {
                TagView stvorena = backend.create(oznaka);
                redirect.addFlashAttribute("poruka",
                        "Oznaka '" + stvorena.name() + "' je stvorena (id " + stvorena.identifier() + ").");
            } else {
                backend.update(id, oznaka);
                redirect.addFlashAttribute("poruka", "Oznaka je azurirana.");
            }
        } catch (BackendException e) {
            redirect.addFlashAttribute("greska", e.getMessage());
        }
        return "redirect:/oznake";
    }

    @PostMapping("/{id}/obrisi")
    public String obrisi(@PathVariable String id, RedirectAttributes redirect) {
        try {
            backend.delete(id);
            redirect.addFlashAttribute("poruka", "Oznaka je obrisana.");
        } catch (BackendException e) {
            redirect.addFlashAttribute("greska", e.getMessage());
        }
        return "redirect:/oznake";
    }

    @ModelAttribute("nacinRada")
    public Map<String, String> nacinRada() {
        try {
            return backend.mode();
        } catch (BackendException e) {
            return Map.of();
        }
    }
}
