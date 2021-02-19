package br.com.zup.propostas.schedule;

import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.CartaoResponse;
import br.com.zup.propostas.proposta.Proposta;
import br.com.zup.propostas.proposta.PropostaRepository;
import br.com.zup.propostas.proposta.StatusProposta;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
public class BuscaNovoCartao {

    private Logger logger = LoggerFactory.getLogger(BuscaNovoCartao.class);
    private PropostaRepository repository;
    private CartaoClient cartaoClient;
    private TransactionTemplate transactionTemplate;

    public BuscaNovoCartao(PropostaRepository repository, CartaoClient cartaoClient, TransactionTemplate transactionTemplate) {
        this.repository = repository;
        this.cartaoClient = cartaoClient;
        this.transactionTemplate = transactionTemplate;
    }

    @Scheduled(fixedDelayString = "${associa-cartao-proposta.periodicidade}")
    public void buscaCartoes() {

        boolean haResultado = true;

        while (haResultado) {
            //noinspection ConstantConditions
            haResultado = transactionTemplate.execute(transaction -> {
                List<Proposta> resultList = repository.findTop5ByStatusOrderByDataCriacao(StatusProposta.ELEGIVEL);
                logger.info("Analisando {} propostas.", resultList.size());

                if (resultList.isEmpty())
                    return false;

                for (Proposta proposta : resultList) {
                    CartaoResponse cartaoResponse = buscarCartao(proposta);
                    if (cartaoResponse == null) continue;

                    proposta.associarCartao(cartaoResponse);
                    proposta.atualizarStatus("ASSOCIADO");
                    repository.save(proposta);

                    logger.info("Cartão associado à proposta {}", proposta.getId());
                }

                return true;
            });
        }
    }

    private CartaoResponse buscarCartao(Proposta proposta) {
        try {
            CartaoResponse response = cartaoClient.buscarCartao(proposta.getId().toString());
            return response;
        } catch (FeignException e) {
            logger.error("Cartão não encontrado para a proposta {}", proposta.getId());
            return null;
        }
    }
}
