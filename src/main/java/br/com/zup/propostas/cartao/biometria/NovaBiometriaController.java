package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class NovaBiometriaController {

    private TransactionExecutor executor;
    private Logger logger = LoggerFactory.getLogger(NovaBiometriaController.class);

    public NovaBiometriaController(TransactionExecutor executor) {
        this.executor = executor;
    }

    @PostMapping("/{id}/biometrias")
    @Transactional
    public ResponseEntity<?> criaBiometria(@PathVariable("id") UUID id,
                                           @RequestBody @Valid NovaBiometriaRequest request,
                                           UriComponentsBuilder uriBuilder) {
        Cartao cartao = executor.find(Cartao.class, id);

        cartao.addBiometria(request.getDigital().getBytes());
        executor.merge(cartao);

        Biometria biometria = cartao.getBiometrias().stream()
                .max(Comparator.comparing(b -> b.getId()))
                .get();

        URI uri = uriBuilder.path("/cartoes/{id}/biometrias/{biometriaId}")
                .buildAndExpand(cartao.getId(), biometria.getId())
                .toUri();

        logger.info("Biometria {} cadastrada com sucesso no cartão {}", biometria.getId(), cartao.getId());

        return ResponseEntity.created(uri).build();
    }
}
