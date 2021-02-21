package br.com.zup.propostas.schedule;

import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.proposta.Proposta;
import br.com.zup.propostas.proposta.PropostaRepository;
import br.com.zup.propostas.proposta.StatusProposta;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
class BuscaNovoCartaoTest {

    @Autowired
    private EntityManager manager;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private BuscaNovoCartao buscaNovoCartao;
    @MockBean
    private PropostaRepository repository;
    @MockBean
    private CartaoClient cartaoClient;

    @Test
    @DisplayName("Deve associar um cartão com sucesso.")
    void teste1() {
        Proposta proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        proposta.atualizarStatus("SEM_RESTRICAO");
        manager.persist(proposta);
        List<Proposta> value = new ArrayList<>();
        value.add(proposta);

        when(repository.findTop5ByStatusOrderByDataCriacao(any(StatusProposta.class))).thenReturn(value, List.of());
        when(cartaoClient.buscarCartao(any(String.class))).thenReturn(TesteDataBuilder.getCartaoResponse());

        buscaNovoCartao.buscaCartoes();

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        Mockito.verify(repository).save(captor.capture());
        assertNotNull(captor.getValue().getCartao());
        assertEquals(StatusProposta.CARTAO_ASSOCIADO, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Não deve associar um cartão quando já houver cartão associado.")
    void teste2() {
        Proposta proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        proposta.atualizarStatus("SEM_RESTRICAO");
        proposta.associarCartao(TesteDataBuilder.getCartaoResponse());
        manager.persist(proposta);
        List<Proposta> value = new ArrayList<>();
        value.add(proposta);

        when(repository.findTop5ByStatusOrderByDataCriacao(any(StatusProposta.class))).thenReturn(value, List.of());
        when(cartaoClient.buscarCartao(any(String.class))).thenReturn(TesteDataBuilder.getCartaoResponse());

        assertThrows(IllegalArgumentException.class, () -> buscaNovoCartao.buscaCartoes());
        Mockito.verify(repository, never()).save(any(Proposta.class));
    }

    @Test
    @DisplayName("Não deve associar um cartão quando houver erro no Feign.")
    void teste3() {
        Proposta proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        proposta.atualizarStatus("SEM_RESTRICAO");
        manager.persist(proposta);
        List<Proposta> value = new ArrayList<>();
        value.add(proposta);

        when(repository.findTop5ByStatusOrderByDataCriacao(any(StatusProposta.class))).thenReturn(value, List.of());
        when(cartaoClient.buscarCartao(any(String.class))).thenThrow(FeignException.class);

        buscaNovoCartao.buscaCartoes();
        Mockito.verify(repository, never()).save(any(Proposta.class));
    }
}