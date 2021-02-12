package br.com.zup.propostas.compartilhado.transaction;

import br.com.zup.propostas.data.TesteDataBuilder;
import br.com.zup.propostas.proposta.Proposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TransactionExecutorTest {

    @Autowired
    private TransactionExecutor executor;

    @Test
    @DisplayName("Deve retornar uma proposta com sucesso.")
    void teste1(){
        Proposta proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        executor.persist(proposta);
        UUID id = proposta.getId();
        Proposta propostaEncontrada = executor.find(Proposta.class, id);

        assertEquals(proposta.getId(), propostaEncontrada.getId());
        assertEquals(proposta.getStatus(), propostaEncontrada.getStatus());
        assertEquals(proposta.getNome(), propostaEncontrada.getNome());
    }

    @Test
    @DisplayName("Deve retornar exceção por não encontrar a proposta.")
    void teste2(){
        Proposta proposta = TesteDataBuilder.getNovaPropostaRequest().toModel();
        executor.persist(proposta);
        UUID id = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> executor.find(Proposta.class, id));
    }

}