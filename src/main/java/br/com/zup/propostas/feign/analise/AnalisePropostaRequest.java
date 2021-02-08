package br.com.zup.propostas.feign.analise;

import br.com.zup.propostas.compartilhado.validacao.CPFouCNPJ.CPFouCNPJ;

import javax.validation.constraints.NotBlank;

public class AnalisePropostaRequest {

    private String nome;
    @NotBlank
    @CPFouCNPJ
    private String documento;
    private String idProposta;

    public AnalisePropostaRequest(String nome, @NotBlank String documento, String idProposta) {
        this.nome = nome;
        this.documento = documento;
        this.idProposta = idProposta;
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public String getIdProposta() {
        return idProposta;
    }
}
