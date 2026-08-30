package hr.algebra.interop.client.web;

import hr.algebra.interop.client.backend.BackendException;
import hr.algebra.interop.client.backend.WeatherClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vrijeme")
public class WeatherController {

    private final WeatherClient weather;

    public WeatherController(WeatherClient weather) {
        this.weather = weather;
    }

    @GetMapping
    public String stranica() {
        return "vrijeme";
    }

    @PostMapping
    public String pretrazi(@RequestParam(required = false) String grad, Model model) {
        model.addAttribute("grad", grad);
        try {
            model.addAttribute("odgovor", weather.temperature(grad));
        } catch (BackendException e) {
            model.addAttribute("greska", e.getMessage());
        }
        return "vrijeme";
    }
}
