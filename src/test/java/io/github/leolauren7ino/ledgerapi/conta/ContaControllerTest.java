package io.github.leolauren7ino.ledgerapi.conta;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        String token = tokenDe("conta.dona@exemplo.com");

        String respostaConta = mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.usuarioId").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        String numero = JsonPath.read(respostaConta, "$.numero");
        assertTrue(numero.matches("\\d{10}"));
    }

    @Test
    void listarDevolveSoAsContasDoDono() throws Exception {
        String token = tokenDe("lista.dona@exemplo.com");

        mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listarNaoMostraContasDeOutraPessoa() throws Exception {
        String tokenAna = tokenDe("ana.lista@exemplo.com");
        String tokenBia = tokenDe("bia.lista@exemplo.com");

        mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/contas")
                        .header("Authorization", "Bearer " + tokenBia))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void buscarContaDeOutraPessoaDevolve404() throws Exception {
        String tokenAna = tokenDe("ana.busca@exemplo.com");
        String tokenBia = tokenDe("bia.busca@exemplo.com");

        String respostaConta = mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(respostaConta, "$.id");

        mockMvc.perform(get("/contas/" + id)
                        .header("Authorization", "Bearer " + tokenBia))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Conta não encontrada"));
    }

    private String tokenDe(String email) throws Exception {
        String corpo = """
                {"email":"%s","senha":"senha-forte-123"}
                """.formatted(email);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated());

        String respostaLogin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(respostaLogin, "$.token");
    }
}