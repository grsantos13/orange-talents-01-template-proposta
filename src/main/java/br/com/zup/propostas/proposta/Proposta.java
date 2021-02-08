package br.com.zup.propostas.proposta;

import br.com.zup.propostas.compartilhado.validacao.CPFouCNPJ.CPFouCNPJ;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @CPFouCNPJ
    @NotBlank
    @Column(nullable = false, unique = true)
    private String documento;

    @NotBlank
    @Column(nullable = false)
    private String endereco;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    private StatusProposta status;

    public Proposta(@NotBlank String nome,
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

    public Long getId() {
        return this.id;
    }

    public void atualizarStatus(String resultadoSolicitacao) {
        this.status = StatusProposta.toEnum(resultadoSolicitacao);
    }
}
