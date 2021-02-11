package br.com.zup.propostas.proposta.acompanhamento;

import br.com.zup.propostas.proposta.Proposta;
import br.com.zup.propostas.proposta.StatusProposta;

import java.util.UUID;

public class AcompanhaPropostaResponse {
    private UUID id;
    private String nome;
    private StatusProposta status;

    public AcompanhaPropostaResponse(Proposta proposta) {
        this.status = proposta.getStatus();
        this.id = proposta.getId();
        this.nome = proposta.getNome();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public StatusProposta getStatus() {
        return status;
    }

}
