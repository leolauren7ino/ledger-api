package io.github.leolauren7ino.ledgerapi.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import io.github.leolauren7ino.ledgerapi.conta.LancamentoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void depositoDeAdminSobeOSaldoEGravaNoLedger() throws Exception {
        String token = tokenDe("admin.deposito@exemplo.com", true);
        Number id = abrirConta(token);
        long antes = lancamentoRepository.count();

        mockMvc.perform(post("/admin/contas/" + id + "/depositos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(100.0));

        assertEquals(antes + 1, lancamentoRepository.count());
    }

    @Test
    void clienteComumNaoPodeDepositarDevolve403() throws Exception {
        String token = tokenDe("cliente.deposito@exemplo.com", false);
        Number id = abrirConta(token);

        mockMvc.perform(post("/admin/contas/" + id + "/depositos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100.00}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void depositoSemTokenDevolve401() throws Exception {
        mockMvc.perform(post("/admin/contas/1/depositos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":100.00}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void valorZeroDevolve400ComOCampoQueFalhou() throws Exception {
        String token = tokenDe("admin.zero@exemplo.com", true);

        mockMvc.perform(post("/admin/contas/1/depositos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.valor").value("O valor deve ser maior que zero"));
    }

    @Test
    void depositoEmContaInexistenteDevolve404() throws Exception {
        String token = tokenDe("admin.inexistente@exemplo.com", true);

        mockMvc.perform(post("/admin/contas/999999999/depositos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":10.00}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Conta não encontrada"));
    }

    private Number abrirConta(String token) throws Exception {
        String resposta = mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private String tokenDe(String email, boolean admin) throws Exception {
        String corpo = """
                {"email":"%s","senha":"senha-forte-123"}
                """.formatted(email);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated());

        if (admin) {
            jdbcTemplate.update("update usuarios set perfil = 'ADMIN' where email = ?", email);
            entityManager.clear();
        }

        String respostaLogin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(respostaLogin, "$.token");
    }
}