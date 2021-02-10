package br.com.zup.propostas.cartao.bloqueio;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.SolicitacaoBloqueioResponse;
import br.com.zup.propostas.proposta.TransactionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class SolicitaBloqueioController {

    @PersistenceContext
    private EntityManager manager;
    private Logger logger = LoggerFactory.getLogger(SolicitaBloqueioController.class);
    private TransactionExecutor executor;
    private CartaoClient cartaoClient;

    public SolicitaBloqueioController(EntityManager manager, TransactionExecutor executor, CartaoClient cartaoClient) {
        this.manager = manager;
        this.executor = executor;
        this.cartaoClient = cartaoClient;
    }

    @PostMapping("/{id}/bloqueios")
    public ResponseEntity<?> solicitarBloqueio(@PathVariable("id") UUID id, HttpServletRequest request) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id " + id);

        String ipCliente = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        logger.info("Tentativa de bloqueio do cartão {} pelo user-agent {}, ip {}",
                cartao.getId(), userAgent, ipCliente);

        if (ipCliente.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "IP não encontrado.");

        if (userAgent.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User-Agent não encontrado");

        SolicitacaoBloqueioResponse bloqueioResponse = cartaoClient.bloquearCartao(cartao.getNumero(),
                new NovoBloqueioRequest("Propostas"));

        logger.info("Notificação ao sistema bancário enviada, com resposta {}", bloqueioResponse.getResultado());

        cartao.bloquear(userAgent, ipCliente);
        executor.merge(cartao);
        logger.info("Bloqueio gerado com sucesso.");

        return ResponseEntity.ok().build();
    }
}
