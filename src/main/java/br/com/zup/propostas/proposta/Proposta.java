package br.com.zup.propostas.proposta;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.compartilhado.validacao.CPFouCNPJ.CPFouCNPJ;
import br.com.zup.propostas.feign.cartao.CartaoResponse;
import br.com.zup.propostas.proposta.endereco.Endereco;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @CPFouCNPJ
    @NotBlank
    @Column(name = "documento", nullable = false, unique = true, columnDefinition = "bytea")
    @ColumnTransformer(forColumn = "documento",
            read = "pgp_sym_decrypt(documento, current_setting('encrypt.key'))",
            write = "pgp_sym_encrypt(?, current_setting('encrypt.key'))"
    )
    private String documento;

    @NotNull
    @Embedded
    @Column(nullable = false)
    private Endereco endereco;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    private StatusProposta status;

    @OneToOne(mappedBy = "proposta", cascade = CascadeType.MERGE)
    private Cartao cartao;

    @Deprecated
    public Proposta() {
    }

    public Proposta(@NotBlank String nome,
                    @Email @NotBlank String email,
                    @NotBlank String documento,
                    @NotNull Endereco endereco,
                    @NotNull @Positive BigDecimal salario) {

        this.nome = nome;
        this.email = email;
        this.documento = documento;
        this.endereco = endereco;
        this.salario = salario;

    }

    public UUID getId() {
        return this.id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public StatusProposta getStatus() {
        return this.status;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void atualizarStatus(String resultadoSolicitacao) {
        Assert.isTrue(this.status == null || this.status.equals(StatusProposta.ELEGIVEL), "Status da proposta já incluído.");
        this.status = StatusProposta.toEnum(resultadoSolicitacao);
    }

    public void associarCartao(CartaoResponse response) {
        Assert.isTrue(this.status.equals(StatusProposta.ELEGIVEL), "Um cartão não pode ser associado a uma proposta não elegível.");
        Assert.isTrue(!this.status.equals(StatusProposta.CARTAO_ASSOCIADO), "Um cartão não pode ser associado a uma proposta com cartão associado.");
        Assert.isNull(this.cartao, "Já existe um cartão associado.");
        this.cartao = new Cartao(this, response.getId(), response.getEmitidoEm(), response.getLimite());
    }

}
