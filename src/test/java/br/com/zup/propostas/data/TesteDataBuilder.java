package br.com.zup.propostas.data;

import br.com.zup.propostas.proposta.EnderecoRequest;
import br.com.zup.propostas.proposta.NovaPropostaRequest;

import java.math.BigDecimal;

public class TesteDataBuilder {

    public static NovaPropostaRequest getNovaPropostaRequest() {
        NovaPropostaRequest request = new NovaPropostaRequest("Gustavo", "gustavo@email.com"
                , "44444444444", new EnderecoRequest("l", "n", "c", "m", "e", "13333333"),
                BigDecimal.TEN);
        return request;
    }
}
