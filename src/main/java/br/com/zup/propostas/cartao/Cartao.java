package br.com.zup.propostas.cartao;

import br.com.zup.propostas.proposta.Proposta;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Cartao {

    /**
     * Id é gerado pelo sistema de cartões (externo)
     */
    @Id
    @Pattern(regexp = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}")
    private String id;

    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(nullable = false)
    private Proposta proposta;

    @NotNull
    @PastOrPresent
    private LocalDateTime emitidoEm;

    @NotNull
    @Positive
    private BigDecimal limite;

    @Deprecated
    public Cartao() {
    }

    public Cartao(@Valid @NotNull Proposta proposta,
                  @NotBlank String number,
                  @NotNull LocalDateTime emitidoEm,
                  @NotNull @Positive BigDecimal limite) {
        this.proposta = proposta;
        this.id = number;
        this.emitidoEm = emitidoEm;
        this.limite = limite;
    }
}
