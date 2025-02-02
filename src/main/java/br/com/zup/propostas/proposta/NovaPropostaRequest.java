package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compartilhado.validacao.CPFouCNPJ.CPFouCNPJ;
import br.com.zup.propostas.proposta.endereco.Endereco;
import br.com.zup.propostas.proposta.endereco.EnderecoRequest;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class NovaPropostaRequest {

    @NotBlank
    private String nome;
    @Email
    @NotBlank
    private String email;
    @CPFouCNPJ
    @NotBlank
    private String documento;
    @NotNull
    private EnderecoRequest endereco;
    @NotNull
    @Positive
    private BigDecimal salario;

    public NovaPropostaRequest(@NotBlank String nome,
                               @Email @NotBlank String email,
                               @NotBlank String documento,
                               @NotNull EnderecoRequest endereco,
                               @NotNull @Positive BigDecimal salario) {
        this.nome = nome;
        this.email = email;
        this.documento = documento;
        this.endereco = endereco;
        this.salario = salario;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getDocumento() {
        return documento;
    }

    public EnderecoRequest getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String documentoOfuscado(){
        return this.documento.substring(this.documento.length() - 5);
    }

    public Proposta toModel() {
        return new Proposta(nome, email, documento, new Endereco(this.endereco), salario);
    }
}
