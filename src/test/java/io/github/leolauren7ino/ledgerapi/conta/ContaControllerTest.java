package io.github.leolauren7ino.ledgerapi.conta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
}