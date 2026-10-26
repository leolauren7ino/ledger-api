package io.github.leolauren7ino.ledgerapi.admin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// [estudo] A "porta do admin": serve só para provar o acesso por perfil.
// [estudo] Repare: este controller NÃO confere perfil nenhum. Quem protege /admin/** é o SecurityConfig (hasRole("ADMIN")).
@RestController
// [estudo] Prefixo das rotas da classe. Junto com o @GetMapping("/ping") forma GET /admin/ping.
@RequestMapping("/admin")
public class AdminController {

    // [estudo] Provado: CUSTOMER recebe 403 (identificado, sem o papel) e ADMIN recebe 200.
    @GetMapping("/ping")
    // [estudo] Map.of cria um mapa pequeno e imutável; o Spring o transforma no JSON {"mensagem":"pong, admin"}.
    public Map<String, String> ping() {
        return Map.of("mensagem", "pong, admin");
    }
}
