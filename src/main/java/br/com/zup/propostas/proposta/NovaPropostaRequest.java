package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compatilhado.validacao.CPFouCNPJ.CPFouCNPJ;

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
    @NotBlank
    private String endereco;
    @NotNull
    @Positive
    private BigDecimal salario;

    public NovaPropostaRequest(@NotBlank String nome,
                               @Email @NotBlank String email,
                               @NotBlank String documento,
                               @NotBlank String endereco,
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

    public String getEndereco() {
        return endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public Proposta toModel() {
        return new Proposta(nome, email, documento, endereco, salario);
    }
}
