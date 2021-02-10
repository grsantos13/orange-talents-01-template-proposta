package br.com.zup.propostas.cartao.bloqueio;

public class NovoBloqueioRequest {
    private String sistemaResponsavel;

    public NovoBloqueioRequest(String sistemaResponsavel) {
        this.sistemaResponsavel = sistemaResponsavel;
    }

    public String getSistemaResponsavel() {
        return sistemaResponsavel;
    }
}
