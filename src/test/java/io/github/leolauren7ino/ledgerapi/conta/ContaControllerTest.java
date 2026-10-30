package io.github.leolauren7ino.ledgerapi.conta;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void abrirContaSemTokenDevolve401() throws Exception {
        mockMvc.perform(post("/contas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void abrirContaComTokenDevolve201SemExporODono() throws Exception {
        String corpo = """
                {"email":"conta.dona@exemplo.com","senha":"senha-forte-123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated());

        String respostaLogin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(respostaLogin, "$.token");

        String respostaConta = mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.usuarioId").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        String numero = JsonPath.read(respostaConta, "$.numero");
        assertTrue(numero.matches("\\d{10}"));
    }
}