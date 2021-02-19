package br.com.zup.propostas.proposta.endereco;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class EnderecoRequest {
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
    public EnderecoRequest() {
    }

    public EnderecoRequest(@NotBlank String logradouro,
                           @NotBlank String numero,
                           String complemento,
                           @NotBlank String municipio,
                           @NotBlank String estado,
                           @NotBlank @Pattern(regexp = "([0-9]{5}-[0-9]{3}|[0-9]{8})") String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.municipio = municipio;
        this.estado = estado;
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getEstado() {
        return estado;
    }

    public String getCep() {
        return cep;
    }
}
