package br.com.zup.propostas.cartao.bloqueio;

import br.com.zup.propostas.cartao.Cartao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Bloqueio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String userAgent;

    @NotBlank
    @Column(nullable = false)
    private String ipCliente;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime momentoSolicitacao = LocalDateTime.now();

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cartao cartao;

    @Deprecated
    public Bloqueio() {
    }

    public Bloqueio(@NotBlank String userAgent,
                    @NotBlank String ipCliente,
                    @Valid @NotNull Cartao cartao) {
        this.userAgent = userAgent;
        this.ipCliente = ipCliente;
        this.cartao = cartao;
    }
}
