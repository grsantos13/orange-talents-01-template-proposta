package br.com.zup.propostas.feign.cartao;

public class SolicitacaoBloqueioResponse {

    private String resultado;

    @Deprecated
    public SolicitacaoBloqueioResponse() {
    }

    public SolicitacaoBloqueioResponse(String resultado) {
        this.resultado = resultado;
    }

    public String getResultado() {
        return resultado;
    }
}
