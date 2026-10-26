package io.github.leolauren7ino.ledgerapi.admin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("mensagem", "pong, admin");
    }
}