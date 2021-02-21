package br.com.zup.propostas.proposta;

import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.analise.AnalisePropostaClient;
import br.com.zup.propostas.feign.analise.AnalisePropostaRequest;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
class AnalisePropostaTest {

    @Autowired
    private EntityManager manager;
    private AnalisePropostaClient analisePropostaClient = mock(AnalisePropostaClient.class);
    private AnaliseProposta analiseProposta = new AnaliseProposta(analisePropostaClient);
    private NovaPropostaRequest request = TesteDataBuilder.getNovaPropostaRequest();
    private Proposta proposta = request.toModel();

    @BeforeEach
    void setUp(){
        manager.persist(proposta);
    }

    @Test
    @DisplayName("Deve retornar proposta sem restrição.")
    void teste1(){

        when(analisePropostaClient.analisarProposta(any(AnalisePropostaRequest.class))).thenReturn(TesteDataBuilder.getAnalisePropostaResponse("SEM_RESTRICAO"));

        String retorno = analiseProposta.analisarProposta(request, proposta);
        assertEquals("SEM_RESTRICAO", retorno);
    }

    @Test
    @DisplayName("Deve retornar proposta com restrição.")
    void teste2(){

        when(analisePropostaClient.analisarProposta(any(AnalisePropostaRequest.class))).thenThrow(FeignException.UnprocessableEntity.class);

        String retorno = analiseProposta.analisarProposta(request, proposta);
        assertEquals("COM_RESTRICAO", retorno);
    }


    @Test
    @DisplayName("Deve dar erro interno por algum problema no Feign.")
    void teste3(){
        when(analisePropostaClient.analisarProposta(any(AnalisePropostaRequest.class))).thenThrow(FeignException.class);

        assertThrows(ResponseStatusException.class,
                () -> analiseProposta.analisarProposta(request, proposta));
    }

}