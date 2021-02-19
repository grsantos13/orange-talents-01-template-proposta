package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.analise.AnalisePropostaClient;
import br.com.zup.propostas.feign.analise.AnalisePropostaRequest;
import br.com.zup.propostas.feign.analise.AnalisePropostaResponse;
import br.com.zup.propostas.proposta.endereco.EnderecoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.opentracing.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.zup.propostas.data.TesteDataBuilder.getNovaPropostaRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = NovaPropostaController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NovaPropostaControllerTest {

    @MockBean
    private TransactionExecutor executor;
    @MockBean
    private ImpedeDocumentoIgualValidator validador;
    @MockBean
    private AnalisePropostaClient analisePropostaClient;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private Tracer tracer;

    @Test
    @DisplayName("Deve salvar com sucesso sem restrição.")
    public void teste1() throws Exception {
        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        NovaPropostaRequest request = getNovaPropostaRequest();
        String json = mapper.writeValueAsString(request);
        UUID id = UUID.randomUUID();
        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(validador.documentoEstaValido(Mockito.any(NovaPropostaRequest.class))).thenReturn(true);
        Mockito.when(analisePropostaClient.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenReturn(new AnalisePropostaResponse("44444444444", "Gustavo", "SEM_RESTRICAO", id.toString()));

        mvc.perform(post("/propostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/propostas/" + id));

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        Mockito.verify(executor).merge(captor.capture());
        assertEquals(StatusProposta.ELEGIVEL, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve retornar erro por documento não estar válido.")
    public void teste2() throws Exception {
        NovaPropostaRequest request = getNovaPropostaRequest();
        String json = mapper.writeValueAsString(request);
        UUID id = UUID.randomUUID();

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());

        mvc.perform(post("/propostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest
    @MethodSource("generator")
    @DisplayName("Testa todas as regras de obrigatoriedade de campos.")
    public void teste3(NovaPropostaRequest request) throws Exception {
        String json = mapper.writeValueAsString(request);
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(validador.documentoEstaValido(Mockito.any(NovaPropostaRequest.class))).thenReturn(true);
        Mockito.when(analisePropostaClient.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenReturn(new AnalisePropostaResponse("44444444444", "Gustavo", "SEM_RESTRICAO", id.toString()));

        mvc.perform(post("/propostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> generator() {
        EnderecoRequest enderecoRequest = new EnderecoRequest("l", "n", "c", "m", "e", "13333333");

        return Stream.of(
                Arguments.of(new NovaPropostaRequest("", "gustavo@email.com", "44444444444", enderecoRequest, BigDecimal.TEN)),
                Arguments.of(new NovaPropostaRequest("Gustavo", "", "44444444444", enderecoRequest, BigDecimal.TEN)),
                Arguments.of(new NovaPropostaRequest("Gustavo", "gustavo", "44444444444", enderecoRequest, BigDecimal.TEN)),
                Arguments.of(new NovaPropostaRequest("Gustavo", "gustavo@email.com", "", enderecoRequest, BigDecimal.TEN)),
                Arguments.of(new NovaPropostaRequest("Gustavo", "gustavo@email.com", "44444444444", null, BigDecimal.TEN)),
                Arguments.of(new NovaPropostaRequest("Gustavo", "gustavo@email.com", "44444444444", enderecoRequest, null))
        );
    }

    @Test
    @DisplayName("Deve salvar com sucesso com restrição.")
    public void teste4() throws Exception {
        NovaPropostaRequest request = getNovaPropostaRequest();
        String json = mapper.writeValueAsString(request);
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(validador.documentoEstaValido(Mockito.any(NovaPropostaRequest.class))).thenReturn(true);
        Mockito.when(analisePropostaClient.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenThrow(FeignException.UnprocessableEntity.class);

        mvc.perform(post("/propostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/propostas/" + id));

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        Mockito.verify(executor).merge(captor.capture());
        assertEquals(StatusProposta.NAO_ELEGIVEL, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve retornar erro ao salvar uma proposta.")
    public void teste5() throws Exception {
        NovaPropostaRequest request = getNovaPropostaRequest();
        String json = mapper.writeValueAsString(request);
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(tracer.activeSpan()).thenReturn(TesteDataBuilder.getSpan());
        Mockito.when(validador.documentoEstaValido(Mockito.any(NovaPropostaRequest.class))).thenReturn(true);
        Mockito.when(analisePropostaClient.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenThrow(FeignException.class);

        mvc.perform(post("/propostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
        Mockito.verify(executor).remove(Mockito.any(Proposta.class));
    }

}