package br.com.zup.propostas.cartao.carteira;

public enum TipoCarteira {
    SAMSUNG("Samsung"), PAYPAL("Paypal");

    private String description;

    TipoCarteira(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
