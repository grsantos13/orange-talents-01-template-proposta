package br.com.zup.propostas.cartao;

import br.com.zup.propostas.proposta.Proposta;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(nullable = false)
    private Proposta proposta;

    @NotBlank
    @Pattern(regexp = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}")
    private String number;

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
                  @NotBlank @CreditCardNumber String number,
                  @NotNull LocalDateTime emitidoEm,
                  @NotNull @Positive BigDecimal limite) {
        this.proposta = proposta;
        this.number = number;
        this.emitidoEm = emitidoEm;
        this.limite = limite;
    }
}
