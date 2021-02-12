package br.com.zup.propostas.cartao.viagem;

import org.springframework.util.Assert;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class NovoAvisoViagemRequest {

    @NotBlank
    private String destino;

    @NotNull
    @Future
    private LocalDate validoAte;


    public NovoAvisoViagemRequest(@NotBlank String destino,
                                  @NotNull @Future LocalDate validoAte) {
        Assert.notNull(destino, "Destino não pode ser nulo.");
        Assert.isTrue(validoAte.isAfter(LocalDate.now()), "A validade da viagem precisa ser no futuro.");
        this.destino = destino;
        this.validoAte = validoAte;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getValidoAte() {
        return validoAte;
    }
}
