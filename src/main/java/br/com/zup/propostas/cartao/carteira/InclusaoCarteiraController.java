package br.com.zup.propostas.cartao.carteira;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.NovaCarteiraResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class InclusaoCarteiraController {

    private EntityManager manager;
    private TransactionExecutor executor;
    private CartaoClient cartaoClient;
    private Logger logger = LoggerFactory.getLogger(InclusaoCarteiraController.class);

    public InclusaoCarteiraController(EntityManager manager, TransactionExecutor executor, CartaoClient cartaoClient) {
        this.manager = manager;
        this.executor = executor;
        this.cartaoClient = cartaoClient;
    }

    @PostMapping("/{id}/carteiras")
    public ResponseEntity<?> incluirCarteira(@PathVariable("id") UUID id,
                                             @RequestBody @Valid NovaCarteiraRequest request,
                                             UriComponentsBuilder uriBuilder) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id " + id);

        logger.info("Iniciando criação de carteira para o cartão {}", cartao.getId());
        NovaCarteiraResponse carteiraResponse = cartaoClient.incluirCarteira(cartao.getNumero(), request);
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
