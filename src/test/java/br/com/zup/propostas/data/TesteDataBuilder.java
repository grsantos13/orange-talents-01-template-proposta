package br.com.zup.propostas.data;

import br.com.zup.propostas.cartao.Cartao;
import br.com.zup.propostas.cartao.carteira.NovaCarteiraRequest;
import br.com.zup.propostas.cartao.carteira.TipoCarteira;
import br.com.zup.propostas.feign.analise.AnalisePropostaResponse;
import br.com.zup.propostas.feign.cartao.CartaoResponse;
import br.com.zup.propostas.feign.cartao.RenegociacaoResponse;
import br.com.zup.propostas.feign.cartao.VencimentoResponse;
import br.com.zup.propostas.proposta.endereco.EnderecoRequest;
import br.com.zup.propostas.proposta.NovaPropostaRequest;
import br.com.zup.propostas.proposta.Proposta;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static AnalisePropostaResponse getAnalisePropostaResponse(String resultado){
        return new AnalisePropostaResponse("00000000000", "nome", resultado, UUID.randomUUID().toString());
    }

    public static CartaoResponse getCartaoResponse(){
        return new CartaoResponse(
                "1111-1111-1111-1111",
                LocalDateTime.now(),
                "Teste",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                BigDecimal.TEN,
                new RenegociacaoResponse(),
                new VencimentoResponse(),
                UUID.randomUUID().toString()
        );
    }

    public static Span getSpan() {
        return new Span() {
            @Override
            public SpanContext context() {
                return null;
            }

            @Override
            public Span setTag(String key, String value) {
                return null;
            }

            @Override
            public Span setTag(String key, boolean value) {
                return null;
            }

            @Override
            public Span setTag(String key, Number value) {
                return null;
            }

            @Override
            public <T> Span setTag(Tag<T> tag, T value) {
                return null;
            }

            @Override
            public Span log(Map<String, ?> fields) {
                return null;
            }

            @Override
            public Span log(long timestampMicroseconds, Map<String, ?> fields) {
                return null;
            }

            @Override
            public Span log(String event) {
                return null;
            }

            @Override
            public Span log(long timestampMicroseconds, String event) {
                return null;
            }

            @Override
            public Span setBaggageItem(String key, String value) {
                return null;
            }

            @Override
            public String getBaggageItem(String key) {
                return null;
            }

            @Override
            public Span setOperationName(String operationName) {
                return null;
            }

            @Override
            public void finish() {

            }

            @Override
            public void finish(long finishMicros) {

            }
        };
    }
}
