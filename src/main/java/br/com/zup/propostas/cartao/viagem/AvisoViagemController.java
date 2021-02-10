package br.com.zup.propostas.cartao.viagem;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.feign.cartao.AvisoViagemResponse;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.proposta.TransactionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class AvisoViagemController {

    private EntityManager manager;
    private TransactionExecutor executor;
    private CartaoClient cartaoClient;
    private Logger logger = LoggerFactory.getLogger(AvisoViagemController.class);

    public AvisoViagemController(EntityManager manager, TransactionExecutor executor, CartaoClient cartaoClient) {
        this.manager = manager;
        this.executor = executor;
        this.cartaoClient = cartaoClient;
    }

    @PostMapping("/{id}/avisos")
    public ResponseEntity<?> avisarViagem(@PathVariable("id") UUID id,
                                          @RequestBody @Valid NovoAvisoViagemRequest request,
                                          HttpServletRequest servletRequest) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id " + id);

        String ipCliente = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");

        logger.info("Aviso de viagem do cartão {} pelo user-agent {}, ip {}",
                cartao.getId(), userAgent, ipCliente);

        if (ipCliente.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "IP não encontrado.");

        if (userAgent.isBlank())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User-Agent não encontrado");

        AvisoViagemResponse avisoViagemResponse = cartaoClient.avisarViagem(cartao.getNumero(), request);
        logger.info("Notificação ao sistema bancário enviada, com resposta {}", avisoViagemResponse.getResultado());
        cartao.avisarViagem(request, ipCliente, userAgent);
        executor.merge(cartao);

        logger.info("Aviso enviado com sucesso.");

        return ResponseEntity.ok().build();
    }
}
