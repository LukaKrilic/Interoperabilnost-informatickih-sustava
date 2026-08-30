package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.SoapSearchClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/soap")
public class SoapController {

    private final SoapSearchClient soap;

    public SoapController(SoapSearchClient soap) {
        this.soap = soap;
    }

    @GetMapping
    public String stranica() {
        return "soap";
    }

    @PostMapping
    public String pretrazi(@RequestParam(required = false) String pojam, Model model) {
        model.addAttribute("pojam", pojam);
        try {
            model.addAttribute("odgovor", soap.pretrazi(pojam));
        } catch (BackendException e) {
            model.addAttribute("greska", e.getMessage());
        }
        return "soap";
    }
}
