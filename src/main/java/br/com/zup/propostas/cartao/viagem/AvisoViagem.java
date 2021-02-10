package br.com.zup.propostas.cartao.viagem;

import br.com.zup.propostas.cartao.Cartao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AvisoViagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cartao cartao;

    @NotBlank
    @Column(nullable = false)
    private String destino;

    @NotNull
    @Future
    private LocalDate validoAte;

    @NotBlank
    @Column(nullable = false)
    private String userAgent;

    @NotBlank
    @Column(nullable = false)
    private String ipCliente;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime momentoAviso = LocalDateTime.now();

    public AvisoViagem(@Valid @NotNull Cartao cartao,
                       @NotBlank String destino,
                       @NotNull @Future LocalDate validoAte,
                       @NotBlank String userAgent,
                       @NotBlank String ipCliente) {
        this.cartao = cartao;
        this.destino = destino;
        this.validoAte = validoAte;
        this.userAgent = userAgent;
        this.ipCliente = ipCliente;
    }
}
