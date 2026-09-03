package hr.algebra.interop.client.web;

import hr.algebra.interop.client.auth.SessionTokens;
import hr.algebra.interop.client.auth.TokenPair;
import hr.algebra.interop.client.config.BackendProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class DashboardController {

    private final SessionTokens sessionTokens;
    private final RestClient rest;

    public DashboardController(SessionTokens sessionTokens, BackendProperties backend) {
        this.sessionTokens = sessionTokens;
        this.rest = RestClient.builder().baseUrl(backend.url()).build();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        TokenPair tokens = sessionTokens.tokens();
        model.addAttribute("tokens", tokens);
        model.addAttribute("accessTokenPreview", tokens.accessToken());
        model.addAttribute("refreshTokenPreview", tokens.refreshToken());
        return "dashboard";
    }

    @PostMapping("/token/refresh")
    public String refresh(RedirectAttributes redirect) {
        String stari = sessionTokens.refreshToken();
        try {
            TokenPair novi = rest.post()
                    .uri("/api/auth/refresh")
                    .body(Map.of("refreshToken", stari))
                    .retrieve()
                    .body(TokenPair.class);

            sessionTokens.store(novi);
            redirect.addFlashAttribute("poruka",
                    "Token osvjezen.");
        } catch (Exception e) {
            redirect.addFlashAttribute("greska", "Osvjezavanje nije uspjelo: " + e.getMessage());
        }
        return "redirect:/";
    }
}
