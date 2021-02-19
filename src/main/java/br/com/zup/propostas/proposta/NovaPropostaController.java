package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compartilhado.exception.ApiErrors;
import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.feign.analise.AnaliseProposta;
import br.com.zup.propostas.feign.analise.AnalisePropostaRequest;
import br.com.zup.propostas.feign.analise.AnalisePropostaResponse;
import feign.FeignException;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/propostas")
public class NovaPropostaController {

    private Logger logger = LoggerFactory.getLogger(NovaPropostaController.class);
    private TransactionExecutor executor;
    private ImpedeDocumentoIgualValidator validador;
    private AnaliseProposta analiseProposta;
    private Tracer tracer;

    public NovaPropostaController(TransactionExecutor executor, ImpedeDocumentoIgualValidator validador, AnaliseProposta analiseProposta, Tracer tracer) {
        this.executor = executor;
        this.validador = validador;
        this.analiseProposta = analiseProposta;
        this.tracer = tracer;
    }

    @PostMapping
    public ResponseEntity<?> criarProposta(@RequestBody @Valid NovaPropostaRequest request,
                                           UriComponentsBuilder uriBuilder) {

        Span span = tracer.activeSpan();
        span.setTag("Solicitante", request.getEmail());

        boolean documentoValido = validador.documentoEstaValido(request);

        if (!documentoValido) {
            logger.error("Já existe uma proposta para o documento {}", request.getDocumento());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Proposta proposta = request.toModel();
        executor.persist(proposta);

        AnalisePropostaRequest propostaRequest = new AnalisePropostaRequest(request.getNome(),
                request.getDocumento(), proposta.getId().toString());

        try {
            AnalisePropostaResponse analiseProposta = this.analiseProposta.analisarProposta(propostaRequest);
            proposta.atualizarStatus(analiseProposta.getResultadoSolicitacao());
        } catch (FeignException.UnprocessableEntity e) {
            proposta.atualizarStatus("COM_RESTRICAO");
        } catch (FeignException e) {
            logger.error("Ocorreu o erro " + e.getMessage() + " ao analisar a solicitação. Tente novamente.");
            executor.remove(proposta);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        executor.merge(proposta);

        URI uri = uriBuilder.path("/propostas/{id}")
                .buildAndExpand(proposta.getId())
                .toUri();

        logger.info("Proposta {} criada com sucesso pelo usuário {}",
                proposta.getId(), request.getEmail());
        return ResponseEntity.created(uri).build();
    }
}
