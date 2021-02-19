package br.com.zup.propostas.proposta;

import br.com.zup.propostas.feign.analise.AnalisePropostaClient;
import br.com.zup.propostas.feign.analise.AnalisePropostaRequest;
import br.com.zup.propostas.feign.analise.AnalisePropostaResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnaliseProposta {

    private Logger logger = LoggerFactory.getLogger(AnaliseProposta.class);

    private AnalisePropostaClient analiseProposta;

    public AnaliseProposta(AnalisePropostaClient analiseProposta) {
        this.analiseProposta = analiseProposta;
    }

    public String analisarProposta(NovaPropostaRequest request, Proposta proposta) {
        AnalisePropostaRequest propostaRequest = new AnalisePropostaRequest(request.getNome(),
                request.getDocumento(), proposta.getId().toString());
        try {
            AnalisePropostaResponse analiseProposta = this.analiseProposta.analisarProposta(propostaRequest);
            return analiseProposta.getResultadoSolicitacao();
        } catch (FeignException.UnprocessableEntity e) {
            return "COM_RESTRICAO";
        } catch (FeignException e) {
            logger.error("Ocorreu o erro " + e.getMessage() + " ao analisar a solicitação. Tente novamente.");
            return null;
        }
    }
}
