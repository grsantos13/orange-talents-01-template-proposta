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

import java.util.List;

@Component
public class BuscaNovoCartao {

    private Logger logger = LoggerFactory.getLogger(BuscaNovoCartao.class);
    private PropostaRepository repository;
    private CartaoClient cartaoClient;

    public BuscaNovoCartao(PropostaRepository repository, CartaoClient cartaoClient) {
        this.repository = repository;
        this.cartaoClient = cartaoClient;
    }

    @Scheduled(fixedDelayString = "${associa-cartao-proposta.periodicidade}")
    public void buscaCartoes() {
        List<Proposta> resultList = repository.findByStatus(StatusProposta.ELEGIVEL);
        logger.info("Há {} propostas para analisar.", resultList.size());

        for (Proposta proposta : resultList) {
            try {
                CartaoResponse response = cartaoClient.buscarCartao(proposta.getId().toString());
                proposta.associarCartao(response);
                proposta.atualizarStatus("ASSOCIADO");
                repository.save(proposta);
                logger.info("Cartão associado à proposta {}", proposta.getId());
            } catch (FeignException e) {
                logger.error("Cartão não encontrado para a proposta {}", proposta.getId());
            }
        }
    }
}
