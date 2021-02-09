package br.com.zup.propostas.feign.cartao;

import java.time.LocalDate;

public class AvisoResponse {
    private LocalDate validoAte;
    private String destino;

    public LocalDate getValidoAte() {
        return validoAte;
    }

    public String getDestino() {
        return destino;
    }
}
