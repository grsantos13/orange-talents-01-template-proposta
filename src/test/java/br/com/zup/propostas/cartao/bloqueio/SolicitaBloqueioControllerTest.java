package br.com.zup.propostas.cartao.bloqueio;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.SolicitacaoBloqueioResponse;
import br.com.zup.propostas.proposta.Proposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.opentracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
@ActiveProfiles("test")
class SolicitaBloqueioControllerTest {

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
    @DisplayName("Deve bloquear com sucesso")
    void teste1() throws Exception {
        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.bloquearCartao(Mockito.any(String.class),
                Mockito.any(NovoBloqueioRequest.class))).thenReturn(new SolicitacaoBloqueioResponse("CRIADO"));

        mvc.perform(post("/cartoes/{id}/bloqueios", cartao.getId())
                .header("User-Agent", "Teste"))
                .andExpect(status().isOk());

        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        Mockito.verify(executor).merge(captor.capture());
        assertEquals(1, captor.getValue().getBloqueios().size());
    }

    @Test
    @DisplayName("Deve retornar 422 por não conter o User-Agent")
    void teste2() throws Exception {
        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.bloquearCartao(Mockito.any(String.class),
                Mockito.any(NovoBloqueioRequest.class))).thenReturn(new SolicitacaoBloqueioResponse("CRIADO"));

        mvc.perform(post("/cartoes/{id}/bloqueios", cartao.getId()))
                .andExpect(status().isUnprocessableEntity());
        Mockito.verify(executor, Mockito.never()).merge(Mockito.any(Cartao.class));
    }

    @Test
    @DisplayName("Deve retornar 422 pelo cartão já estar bloqueado")
    void teste3() throws Exception {
        cartao.bloquear("Teste", "127.0.0.1");

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.bloquearCartao(Mockito.any(String.class),
                Mockito.any(NovoBloqueioRequest.class))).thenReturn(new SolicitacaoBloqueioResponse("CRIADO"));

        mvc.perform(post("/cartoes/{id}/bloqueios", cartao.getId()))
                .andExpect(status().isUnprocessableEntity());
        Mockito.verify(executor, Mockito.never()).merge(Mockito.any(Cartao.class));
    }

    @Test
    @DisplayName("Não deve bloquear o cartão por ter o retorno de um erro no Feign")
    void teste4() throws Exception {
        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(executor.find(Mockito.any(), Mockito.any(UUID.class))).thenReturn(cartao);
        Mockito.when(cartaoClient.bloquearCartao(Mockito.any(String.class),
                Mockito.any(NovoBloqueioRequest.class))).thenThrow(FeignException.class);

        mvc.perform(post("/cartoes/{id}/bloqueios", cartao.getId()))
                .andExpect(status().isUnprocessableEntity());
        Mockito.verify(executor, Mockito.never()).merge(Mockito.any(Cartao.class));
    }

}