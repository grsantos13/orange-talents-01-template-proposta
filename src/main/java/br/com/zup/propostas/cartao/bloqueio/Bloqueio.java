package br.com.zup.propostas.cartao.bloqueio;

import br.com.zup.propostas.cartao.Cartao;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBloqueio status;

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
        Assert.notNull(userAgent, "User-Agent não pode ser nulo.");
        Assert.notNull(ipCliente, "IP do cliente não pode ser nulo.");
        Assert.notNull(cartao, "Cartão não pode ser nulo.");
        this.userAgent = userAgent;
        this.ipCliente = ipCliente;
        this.cartao = cartao;
        this.status = StatusBloqueio.BLOQUEADO;
    }

    public StatusBloqueio getStatus() {
        return status;
    }
}
