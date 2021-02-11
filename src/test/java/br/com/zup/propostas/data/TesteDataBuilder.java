package br.com.zup.propostas.data;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.cartao.carteira.NovaCarteiraRequest;
import br.com.zup.propostas.cartao.carteira.TipoCarteira;
import br.com.zup.propostas.proposta.EnderecoRequest;
import br.com.zup.propostas.proposta.NovaPropostaRequest;
import br.com.zup.propostas.proposta.Proposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TesteDataBuilder {

    public static NovaPropostaRequest getNovaPropostaRequest() {
        NovaPropostaRequest request = new NovaPropostaRequest("Gustavo", "gustavo@email.com"
                , "44444444444", new EnderecoRequest("l", "n", "c", "m", "e", "13333333"),
                BigDecimal.TEN);
        return request;
    }

    public static Cartao getCartao(Proposta proposta){
        return new Cartao(proposta, "1111-1111-1111-1111", LocalDateTime.now(), BigDecimal.TEN);
    }

    public static NovaCarteiraRequest getNovaCarteiraRequest(TipoCarteira tipo){
        return new NovaCarteiraRequest("gustavo@gmail.com", tipo);
    }
}
