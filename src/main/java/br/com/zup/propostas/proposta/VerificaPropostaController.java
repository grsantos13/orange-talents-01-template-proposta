package br.com.zup.propostas.proposta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@RestController
    @RequestMapping("/propostas")
public class VerificaPropostaController {

    @PersistenceContext
    private EntityManager manager;

    @GetMapping("/{id}")
    public ResponseEntity<StatusPropostaResponse> consultar(@PathVariable("id") UUID id){
        Proposta proposta = manager.find(Proposta.class, id);
        if (proposta == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposta não encontrada para o id " + id);

        return ResponseEntity.ok(new StatusPropostaResponse(proposta));
    }
}
