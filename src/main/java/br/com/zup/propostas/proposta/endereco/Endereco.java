package br.com.zup.propostas.proposta.endereco;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Embeddable
public class Endereco {
    @NotBlank
    private String logradouro;

    @NotBlank
    private String numero;

    private String complemento;

    @NotBlank
    private String municipio;

    @NotBlank
    private String estado;

    @NotBlank
    @Pattern(regexp = "([0-9]{5}-[0-9]{3}|[0-9]{8})")
    private String cep;

    @Deprecated
    public Endereco() {
    }

    public Endereco(@NotNull EnderecoRequest endereco) {
        this.logradouro = endereco.getLogradouro();
        this.numero = endereco.getNumero();
        this.complemento = endereco.getComplemento();
        this.municipio = endereco.getMunicipio();
        this.estado = endereco.getEstado();
        this.cep = endereco.getCep();
    }
}
