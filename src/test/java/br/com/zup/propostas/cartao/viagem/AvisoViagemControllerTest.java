package br.com.zup.propostas.cartao.viagem;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.proposta.Proposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.opentracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
@ActiveProfiles("test")
class AvisoViagemControllerTest {

    @Autowired
    private EntityManager manager;
    @MockBean
    private TransactionExecutor executor;
    @MockBean
    private CartaoClient cartaoClient;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private Tracer tracer;

    private Proposta proposta;
    private Cartao cartao;

    @BeforeEach
    public void setup() {
        proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        manager.persist(proposta);
        cartao = TesteDataBuilder.getCartao(proposta);
        manager.persist(cartao);
    }

    @Test
    @DisplayName("Deve avisar a viagem com sucesso.")
    void teste1() throws Exception {
        NovoAvisoViagemRequest request = new NovoAvisoViagemRequest("França", LocalDate.now().plusDays(5));
        String json = mapper.writeValueAsString(request);

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.avisarViagem(Mockito.any(String.class), Mockito.any(NovoAvisoViagemRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/avisos", cartao.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("User-Agent", "Teste")
        .content(json)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Não deve avisar a viagem com sucesso devido a não ter user-agent.")
    void teste2() throws Exception {
        NovoAvisoViagemRequest request = new NovoAvisoViagemRequest("França", LocalDate.now().plusDays(5));
        String json = mapper.writeValueAsString(request);

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.avisarViagem(Mockito.any(String.class), Mockito.any(NovoAvisoViagemRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/avisos", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Não deve salvar o aviso caso haja problema no retorno do Feign.")
    void teste3() throws Exception {
        NovoAvisoViagemRequest request = new NovoAvisoViagemRequest("França", LocalDate.now().plusDays(5));
        String json = mapper.writeValueAsString(request);

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.avisarViagem(Mockito.any(String.class), Mockito.any(NovoAvisoViagemRequest.class))).thenThrow(FeignException.class);

        mvc.perform(post("/cartoes/{id}/avisos", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isUnprocessableEntity());

        Mockito.verify(executor, Mockito.never()).merge(Mockito.any(Cartao.class));
    }


}