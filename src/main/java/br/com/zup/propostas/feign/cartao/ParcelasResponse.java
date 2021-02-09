package br.com.zup.propostas.feign.cartao;

import java.math.BigDecimal;

public class ParcelasResponse {
    private String id;
    private int quantidade;
    private BigDecimal valor;

    public String getId() {
        return id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
