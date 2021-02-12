package br.com.zup.propostas.cartao.carteira;

import br.com.zup.propostas.cartao.Cartao;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Carteira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotNull
    private LocalDateTime associadaEm = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCarteira emissor;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cartao cartao;

    @Deprecated
    public Carteira() {
    }

    public Carteira(@NotBlank String email,
                    @NotNull TipoCarteira emissor,
                    @Valid @NotNull Cartao cartao) {
        this.email = email;
        this.emissor = emissor;
        this.cartao = cartao;
    }

    public Long getId() {
        return id;
    }
}
