package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.proposta.Proposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

import java.util.Base64;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
class NovaBiometriaControllerTest {

    @Autowired
    private TransactionExecutor executor;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Proposta proposta;
    private Cartao cartao;

    @BeforeEach
    public void setup() {
        proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        executor.persist(proposta);
        cartao = TesteDataBuilder.getCartao(proposta);
        executor.persist(cartao);
    }

    @Test
    @DisplayName("Deve criar a biometria com sucesso.")
    void teste1() throws Exception {

        String biometria = Base64.getEncoder().encodeToString("digital".getBytes());
        NovaBiometriaRequest request = new NovaBiometriaRequest(biometria);
        String json = mapper.writeValueAsString(request);
        mvc.perform(post("/cartoes/{id}/biometrias", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar bad request por ter campo nulo.")
    void teste2() throws Exception {
        NovaBiometriaRequest request = new NovaBiometriaRequest(null);
        String json = mapper.writeValueAsString(request);

        mvc.perform(post("/cartoes/{id}/biometrias", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve dar erro pela biometria não estar em Base64.")
    void teste3() throws Exception {
        NovaBiometriaRequest request = new NovaBiometriaRequest("digital");
        String json = mapper.writeValueAsString(request);

        mvc.perform(post("/cartoes/{id}/biometrias", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("fieldErrors[0].field").value("digital"));
    }

}