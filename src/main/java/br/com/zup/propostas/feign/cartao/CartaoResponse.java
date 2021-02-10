package br.com.zup.propostas.feign.cartao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartaoResponse {
    private String id;
    private LocalDateTime emitidoEm;
    private String titular;
    private List<NovoBloqueioResponse> bloqueios;
    private List<AvisoResponse> avisos;
    private List<CarteiraResponse> carteiras;
    private List<ParcelasResponse> parcelas;
    private BigDecimal limite;
    private RenegociacaoResponse renegociacao;
    private VencimentoResponse vencimento;
    private String idProposta;

    public String getId() {
        return id;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public String getTitular() {
        return titular;
    }

    public List<NovoBloqueioResponse> getBloqueios() {
        return bloqueios;
    }

    public List<AvisoResponse> getAvisos() {
        return avisos;
    }

    public List<CarteiraResponse> getCarteiras() {
        return carteiras;
    }

    public List<ParcelasResponse> getParcelas() {
        return parcelas;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public RenegociacaoResponse getRenegociacao() {
        return renegociacao;
    }

    public VencimentoResponse getVencimento() {
        return vencimento;
    }

    public String getIdProposta() {
        return idProposta;
    }
}
