package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.BackendImportClient;
import hr.algebra.interop.client.backend.ImportResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/uvoz")
public class ImportController {

    private final BackendImportClient backend;

    public ImportController(BackendImportClient backend) {
        this.backend = backend;
    }

    @GetMapping
    public String stranica() {
        return "uvoz";
    }

    @PostMapping
    public String uvezi(@RequestParam("datoteka") MultipartFile datoteka, Model model) {
        if (datoteka.isEmpty()) {
            model.addAttribute("greska", "Niste odabrali datoteku.");
            return "uvoz";
        }

        model.addAttribute("imeDatoteke", datoteka.getOriginalFilename());
        try {
            ImportResult rezultat = backend.uvezi(datoteka.getOriginalFilename(), datoteka.getBytes());
            model.addAttribute("rezultat", rezultat);
        } catch (BackendException e) {
            model.addAttribute("greska", e.getMessage());
        } catch (IOException e) {
            model.addAttribute("greska", "Datoteku nije moguce procitati: " + e.getMessage());
        }
        return "uvoz";
    }
}
