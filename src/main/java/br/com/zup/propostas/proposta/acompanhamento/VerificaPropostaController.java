package br.com.zup.propostas.proposta.acompanhamento;

import br.com.zup.propostas.compartilhado.transaction.TransactionExecutor;
import br.com.zup.propostas.proposta.Proposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/propostas")
public class VerificaPropostaController {

    private TransactionExecutor executor;

    public VerificaPropostaController(TransactionExecutor executor) {
        this.executor = executor;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusPropostaResponse> consultar(@PathVariable("id") UUID id) {
        Proposta proposta = executor.find(Proposta.class, id);
        return ResponseEntity.ok(new StatusPropostaResponse(proposta));
    }
}
