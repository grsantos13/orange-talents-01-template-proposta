package br.com.zup.propostas.cartao;

import br.com.zup.propostas.cartao.biometria.Biometria;
import br.com.zup.propostas.cartao.bloqueio.Bloqueio;
import br.com.zup.propostas.cartao.viagem.AvisoViagem;
import br.com.zup.propostas.cartao.viagem.NovoAvisoViagemRequest;
import br.com.zup.propostas.proposta.Proposta;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Valid
    @NotNull
    @OneToOne
    @JoinColumn(nullable = false)
    private Proposta proposta;

    @NotBlank
    @Pattern(regexp = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}")
    private String numero;

    @NotNull
    private LocalDateTime emitidoEm;

    @NotNull
    @Positive
    private BigDecimal limite;

    @Valid
    @OneToMany(mappedBy = "cartao", cascade = CascadeType.MERGE)
    private Set<Biometria> biometrias;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.MERGE)
    private List<Bloqueio> bloqueios;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.MERGE)
    private List<AvisoViagem> viagens;

    @Deprecated
    public Cartao() {
    }

    public Cartao(@Valid @NotNull Proposta proposta,
                  @NotBlank String numero,
                  @NotNull LocalDateTime emitidoEm,
                  @NotNull @Positive BigDecimal limite) {
        this.proposta = proposta;
        this.numero = numero;
        this.emitidoEm = emitidoEm;
        this.limite = limite;
    }

    public UUID getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public Set<Biometria> getBiometrias() {
        return biometrias;
    }

    public void addBiometria(String biometria) {
        this.biometrias.add(new Biometria(biometria, this));
    }

    public void bloquear(@NotBlank String userAgent, @NotBlank String ipCliente) {
        Assert.notNull(userAgent, "User-Agent não pode ser nulo");
        Assert.notNull(ipCliente, "IP do Cliente não pode ser nulo");
        this.bloqueios.add(new Bloqueio(userAgent, ipCliente, this));
    }

    public void avisarViagem(NovoAvisoViagemRequest request, String ipCliente, String userAgent) {
        this.viagens.add(new AvisoViagem(this, request.getDestino(), request.getValidoAte(), userAgent, ipCliente));
    }
}
