package br.com.zup.propostas.cartao.viagem;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class AvisoViagemController {

    private TransactionExecutor executor;
    private CartaoClient cartaoClient;
    private Logger logger = LoggerFactory.getLogger(AvisoViagemController.class);

    public AvisoViagemController(TransactionExecutor executor, CartaoClient cartaoClient) {
        this.executor = executor;
        this.cartaoClient = cartaoClient;
    }

    @PostMapping("/{id}/avisos")
    public ResponseEntity<?> avisarViagem(@PathVariable("id") UUID id,
                                          @RequestBody @Valid NovoAvisoViagemRequest request,
                                          HttpServletRequest servletRequest) {
        Cartao cartao = executor.find(Cartao.class, id);

        String ipCliente = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");

        logger.info("Aviso de viagem do cartão {} pelo user-agent {}, ip {}",
                cartao.getId(), userAgent, ipCliente);

        if (ipCliente.isBlank() || userAgent.isBlank())
            return ResponseEntity.unprocessableEntity().build();

        cartaoClient.avisarViagem(cartao.getNumero(), request);
        logger.info("Notificação ao sistema bancário enviada");

        cartao.avisarViagem(request, ipCliente, userAgent);
        executor.merge(cartao);

        logger.info("Aviso enviado com sucesso.");

        return ResponseEntity.ok().build();
    }
}