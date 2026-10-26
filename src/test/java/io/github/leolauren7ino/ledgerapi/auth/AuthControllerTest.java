package io.github.leolauren7ino.ledgerapi.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// [estudo] Teste de integração: sobe o Spring inteiro, com banco, segurança e o TratadorDeErros.
@SpringBootTest
// [estudo] No Spring Boot 4 o MockMvc não vem ligado sozinho: esta anotação o liga.
@AutoConfigureMockMvc
// [estudo] Desfaz no banco o que cada teste gravou, para o teste poder rodar de novo sem dar 409.
@Transactional
class AuthControllerTest {

    // [estudo] Simula uma chamada HTTP sem subir o servidor.
    @Autowired
    private MockMvc mockMvc;

    @Test
    void cadastroValidoDevolve201ComEmailEmMinusculas() throws Exception {
        String corpo = """
                {"email":"Teste.Cadastro@Exemplo.com","senha":"senha-forte-123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("teste.cadastro@exemplo.com"));
    }

        @Test
    void senhaCurtaDevolve400ComOCampoQueFalhou() throws Exception {
        String corpo = """
                {"email":"senha.curta@exemplo.com","senha":"123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.senha").value("A senha deve ter entre 8 e 72 caracteres"));
    }

    @Test
    void emailRepetidoDevolve409() throws Exception {
        String corpo = """
                {"email":"repetido@exemplo.com","senha":"senha-forte-123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("E-mail já cadastrado"));
    }
}