package br.com.zup.propostas.cartao.carteira;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.exception.ApiErrors;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class InclusaoCarteiraController {

    private TransactionExecutor executor;
    private CartaoClient cartaoClient;
    private CarteiraRepetidaValidator carteiraRepetidaValidator;
    private Tracer tracer;
    private Logger logger = LoggerFactory.getLogger(InclusaoCarteiraController.class);

    public InclusaoCarteiraController(TransactionExecutor executor, CartaoClient cartaoClient, CarteiraRepetidaValidator carteiraRepetidaValidator, Tracer tracer) {
        this.executor = executor;
        this.cartaoClient = cartaoClient;
        this.carteiraRepetidaValidator = carteiraRepetidaValidator;
        this.tracer = tracer;
    }

    @PostMapping("/{id}/carteiras")
    public ResponseEntity<?> incluirCarteira(@PathVariable("id") UUID id,
                                             @RequestBody @Valid NovaCarteiraRequest request,
                                             UriComponentsBuilder uriBuilder) {
        Cartao cartao = executor.find(Cartao.class, id);


        Span span = tracer.activeSpan();
        span.setTag("Solicitante", cartao.getProposta().getEmail());

        boolean carteiraValida = carteiraRepetidaValidator.carteiraValida(request);
        if (!carteiraValida)
            return ResponseEntity.unprocessableEntity().body(new ApiErrors("carteira",
                    "Carteira " + request.getCarteira() + " já associada ao cartão."));

        logger.info("Iniciando criação de carteira para o cartão {}", cartao.getId());

        cartaoClient.incluirCarteira(cartao.getNumero(), request);
        cartao.addCarteira(request);
        executor.merge(cartao);

        Long carteiraId = cartao.getCarteiras().stream()
                .max(Comparator.comparing(carteira -> carteira.getId()))
                .get().getId();

        logger.info("Carteira {} incluída com sucesso no cartão {}", carteiraId, cartao.getId());

        URI uri = uriBuilder.path("/cartoes/{id}/{carteiraId}")
                .buildAndExpand(cartao.getId(), carteiraId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }
}
