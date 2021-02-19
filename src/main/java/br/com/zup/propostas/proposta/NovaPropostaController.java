package br.com.zup.propostas.proposta;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private Logger logger = LoggerFactory.getLogger(NovaPropostaController.class);
    private TransactionTemplate transactionTemplate;
    private ImpedeDocumentoIgualValidator validador;
    private AnaliseProposta analiseProposta;
    private EntityManager manager;
    private Tracer tracer;

    public NovaPropostaController(TransactionTemplate transactionTemplate, ImpedeDocumentoIgualValidator validador, AnaliseProposta analiseProposta, Tracer tracer, EntityManager manager) {
        this.transactionTemplate = transactionTemplate;
        this.validador = validador;
        this.analiseProposta = analiseProposta;
        this.tracer = tracer;
        this.manager = manager;
    }

    @PostMapping
    public ResponseEntity<?> criarProposta(@RequestBody @Valid NovaPropostaRequest request,
                                           UriComponentsBuilder uriBuilder) {

        Span span = tracer.activeSpan();
        span.setTag("Solicitante", request.getEmail());

        boolean documentoValido = validador.documentoEstaValido(request);

        if (!documentoValido) {
            logger.error("Já existe uma proposta para o documento ...{}", request.documentoOfuscado());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Já existe uma proposta para o documento ..." + request.documentoOfuscado());
        }

        Proposta proposta = request.toModel();
        transactionTemplate.execute(transaction -> {
            manager.persist(proposta);
            String status = analiseProposta.analisarProposta(request, proposta);
            proposta.atualizarStatus(status);
            manager.merge(proposta);
            return true;
        });

        URI uri = uriBuilder.path("/propostas/{id}")
                .buildAndExpand(proposta.getId())
                .toUri();

        logger.info("Proposta {} criada com sucesso pelo usuário {}",
                proposta.getId(), request.getEmail());

        return ResponseEntity.created(uri).build();

    }

}
