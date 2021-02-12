package br.com.zup.propostas.cartao.carteira;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.proposta.Proposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InclusaoCarteiraControllerTest {

    @Autowired
    private TransactionExecutor executor;
    @MockBean
    private CartaoClient cartaoClient;
    @Autowired
    private CarteiraRepetidaValidator carteiraRepetidaValidator;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

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
    @DisplayName("Deve salvar uma carteira com sucesso.")
    void teste1() throws Exception {
        NovaCarteiraRequest request = TesteDataBuilder.getNovaCarteiraRequest(TipoCarteira.PAYPAL);
        String json = mapper.writeValueAsString(request);
        Mockito.when(cartaoClient.incluirCarteira(Mockito.any(String.class), Mockito.any(NovaCarteiraRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/carteiras", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Não deve salvar uma carteira já existente.")
    void teste2() throws Exception {
        NovaCarteiraRequest request = TesteDataBuilder.getNovaCarteiraRequest(TipoCarteira.PAYPAL);
        cartao.addCarteira(request);
        executor.merge(cartao);
        String json = mapper.writeValueAsString(request);
        Mockito.when(cartaoClient.incluirCarteira(Mockito.any(String.class), Mockito.any(NovaCarteiraRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/carteiras", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest
    @MethodSource("generator")
    @DisplayName("Deve retornar bad request por não preencher o e-mail, ou for inválido.")
    void teste3(NovaCarteiraRequest request) throws Exception {
        String json = mapper.writeValueAsString(request);
        Mockito.when(cartaoClient.incluirCarteira(Mockito.any(String.class), Mockito.any(NovaCarteiraRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/carteiras", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> generator() {
        return Stream.of(
                Arguments.of(new NovaCarteiraRequest("", TipoCarteira.PAYPAL)),
                Arguments.of(new NovaCarteiraRequest("gustavo", TipoCarteira.PAYPAL))
        );
    }


    @Test
    @DisplayName("Deve retornar bad request por não preencher tipo da carteira.")
    void teste4() throws Exception {
        String json = "{\"email\": \"gustavo@email.com\", \"carteira\": null}";
        Mockito.when(cartaoClient.incluirCarteira(Mockito.any(String.class), Mockito.any(NovaCarteiraRequest.class))).thenReturn(null);

        mvc.perform(post("/cartoes/{id}/carteiras", cartao.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isBadRequest());
    }

}