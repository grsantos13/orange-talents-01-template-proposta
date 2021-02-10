package br.com.zup.propostas.cartao.biometria;

import br.com.zup.propostas.cartao.Cartao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Entity
public class Biometria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private byte[] biometria;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cartao cartao;

    @NotNull
    private LocalDateTime dataDeCriacao = LocalDateTime.now();

    @Deprecated
    public Biometria() {
    }

    public Biometria(String biometria, Cartao cartao) {
        this.biometria = Base64.getEncoder().encode(biometria.getBytes());
        this.cartao = cartao;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Biometria biometria1 = (Biometria) o;
        return Arrays.equals(biometria, biometria1.biometria);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(biometria);
    }
}
