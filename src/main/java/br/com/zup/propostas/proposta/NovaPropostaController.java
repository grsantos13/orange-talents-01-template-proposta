package br.com.zup.propostas.proposta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private Logger logger = LoggerFactory.getLogger(NovaPropostaController.class);
    private EntityManager manager;
    private ImpedeDocumentoIgualValidator validador;

    public NovaPropostaController(EntityManager manager, ImpedeDocumentoIgualValidator validador) {
        this.manager = manager;
        this.validador = validador;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> criarProposta(@RequestBody @Valid NovaPropostaRequest request,
                                           UriComponentsBuilder uriBuilder) {

        boolean documentoValido = validador.documentoEstaValido(request);

        if (!documentoValido) {
            logger.error("Já existe uma proposta para o documento {}", request.getDocumento());
            return ResponseEntity.unprocessableEntity().build();
        }

        Proposta proposta = request.toModel();
        manager.persist(proposta);

        URI uri = uriBuilder.path("/propostas/{id}")
                .buildAndExpand(proposta.getId())
                .toUri();

        logger.info("Proposta {} criada com sucesso pelo usuário {}",
                proposta.getId(), request.getEmail());
        return ResponseEntity.created(uri).build();
    }
}
