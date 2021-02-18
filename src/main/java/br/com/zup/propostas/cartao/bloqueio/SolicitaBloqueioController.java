package br.com.zup.propostas.cartao.bloqueio;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.exception.ApiErrors;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.SolicitacaoBloqueioResponse;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class SolicitaBloqueioController {

    private Logger logger = LoggerFactory.getLogger(SolicitaBloqueioController.class);
    private TransactionExecutor executor;
    private CartaoClient cartaoClient;
    private Tracer tracer;

    public SolicitaBloqueioController(TransactionExecutor executor, CartaoClient cartaoClient, Tracer tracer) {
        this.executor = executor;
        this.cartaoClient = cartaoClient;
        this.tracer = tracer;
    }

    @PostMapping("/{id}/bloqueios")
    public ResponseEntity<?> solicitarBloqueio(@PathVariable("id") UUID id, HttpServletRequest request) {
        Cartao cartao = executor.find(Cartao.class, id);

        Span span = tracer.activeSpan();
        span.setTag("Solicitante", cartao.getProposta().getEmail());

        String ipCliente = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        if (ipCliente == null || userAgent == null)
            return ResponseEntity.unprocessableEntity().body(new ApiErrors("IP ou header User-Agent não encontrados."));

        logger.info("Tentativa de bloqueio do cartão {} pelo user-agent {}, ip {}",
                cartao.getId(), userAgent, ipCliente);

        if (cartao.estaBloqueado())
            return ResponseEntity.unprocessableEntity().body(new ApiErrors("Cartão " + cartao.getId() + " já está bloqueado."));

        SolicitacaoBloqueioResponse bloqueioResponse = cartaoClient.bloquearCartao(cartao.getNumero(),
                new NovoBloqueioRequest("Propostas"));

        logger.info("Notificação ao sistema bancário enviada, com resposta {}", bloqueioResponse.getResultado());

        cartao.bloquear(userAgent, ipCliente);
        executor.merge(cartao);

        logger.info("Bloqueio gerado com sucesso.");

        return ResponseEntity.ok().build();
    }
}
