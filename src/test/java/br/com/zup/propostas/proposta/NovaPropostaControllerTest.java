package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.feign.analise.AnaliseProposta;
import br.com.zup.propostas.feign.analise.AnalisePropostaRequest;
import br.com.zup.propostas.feign.analise.AnalisePropostaResponse;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static br.com.zup.propostas.data.TesteDataBuilder.getNovaPropostaRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NovaPropostaControllerTest {

    private TransactionExecutor executor = Mockito.mock(TransactionExecutor.class);
    private ImpedeDocumentoIgualValidator validador = Mockito.mock(ImpedeDocumentoIgualValidator.class);
    private AnaliseProposta analiseProposta = Mockito.mock(AnaliseProposta.class);
    private NovaPropostaController controller = new NovaPropostaController(executor, validador, analiseProposta);
    private UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

    @Test
    @DisplayName("Deve salvar com sucesso.")
    public void teste1() {
        NovaPropostaRequest request = getNovaPropostaRequest();
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(validador.documentoEstaValido(request)).thenReturn(true);
        Mockito.when(analiseProposta.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenReturn(new AnalisePropostaResponse("44444444444", "Gustavo", "SEM_RESTRICAO", id.toString()));

        ResponseEntity<?> responseEntity = controller.criarProposta(request, uriBuilder);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("http://localhost:8080/propostas/" + id,
                responseEntity.getHeaders().get("Location").get(0));
    }

    @Test
    @DisplayName("Não deve salvar com documento inválido.")
    public void teste2() {
        NovaPropostaRequest request = getNovaPropostaRequest();
        ResponseEntity<?> responseEntity = controller.criarProposta(request, uriBuilder);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Deve salvar proposta com restrição.")
    public void teste3() {
        NovaPropostaRequest request = getNovaPropostaRequest();
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(validador.documentoEstaValido(request)).thenReturn(true);
        Mockito.when(analiseProposta.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenThrow(FeignException.UnprocessableEntity.class);
        ResponseEntity<?> responseEntity = controller.criarProposta(request, uriBuilder);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("http://localhost:8080/propostas/" + id,
                responseEntity.getHeaders().get("Location").get(0));

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        Mockito.verify(executor).merge(captor.capture());
        assertEquals(captor.getValue().getStatus(), StatusProposta.NAO_ELEGIVEL);
    }

    @Test
    @DisplayName("Deve retornar erro.")
    public void teste4() {
        NovaPropostaRequest request = getNovaPropostaRequest();
        UUID id = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            Proposta proposta = invocation.getArgument(0);
            ReflectionTestUtils.setField(proposta, "id", id);
            return proposta;
        }).when(executor).persist(Mockito.any(Proposta.class));

        Mockito.when(validador.documentoEstaValido(request)).thenReturn(true);
        Mockito.when(analiseProposta.analisarProposta(Mockito.any(AnalisePropostaRequest.class))).thenThrow(FeignException.class);
        assertThrows(ResponseStatusException.class, () -> controller.criarProposta(request, uriBuilder));
        Mockito.verify(executor).remove(Mockito.any(Proposta.class));
    }


}