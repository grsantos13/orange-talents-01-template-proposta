package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.cartao.Cartao;
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
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class NovaBiometriaController {

    @PersistenceContext
    private EntityManager manager;
    private Logger logger = LoggerFactory.getLogger(NovaBiometriaController.class);

    @PostMapping("/{id}/biometrias")
    @Transactional
    public ResponseEntity<?> criaBiometria(@PathVariable("id") UUID id,
                                           @RequestBody @Valid NovaBiometriaRequest request,
                                           UriComponentsBuilder uriBuilder) {
        Cartao cartao = manager.find(Cartao.class, id);
        if (cartao == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cartão não encontrado para o id " + id);

        cartao.addBiometria(request.getDigital());
        manager.merge(cartao);

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
