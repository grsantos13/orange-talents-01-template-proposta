package br.com.zup.propostas.schedule;

import br.com.zup.propostas.feign.cartao.CartaoClient;
import br.com.zup.propostas.feign.cartao.CartaoResponse;
import br.com.zup.propostas.proposta.Proposta;
import br.com.zup.propostas.proposta.StatusProposta;
import br.com.zup.propostas.proposta.TransactionExecutor;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class BuscaNovoCartao {

    private Logger logger = LoggerFactory.getLogger(BuscaNovoCartao.class);
    private TransactionExecutor executor;
    private EntityManager manager;
    private CartaoClient cartaoClient;

    public BuscaNovoCartao(TransactionExecutor executor, EntityManager manager, CartaoClient cartaoClient) {
        this.executor = executor;
        this.manager = manager;
        this.cartaoClient = cartaoClient;
    }

    @Scheduled(fixedDelayString = "${associa-cartao-proposta.periodicidade}")
    public void buscaCartoes() {
        List<Proposta> resultList = getPropostas();
        logger.info("Há {} propostas para analisar.", resultList.size());

        for (Proposta proposta : resultList) {
            try {
                CartaoResponse response = cartaoClient.buscarCartao(proposta.getId().toString());
                proposta.associarCartao(response);
                executor.merge(proposta);
                logger.info("Cartão associado à proposta {}", proposta.getId());
            } catch (FeignException e) {
                logger.error("Cartão não encontrado para a proposta {}", proposta.getId());
            }
        }
    }

    private List<Proposta> getPropostas() {
        TypedQuery<Proposta> query = manager.createQuery("select p from Proposta p left join p.cartao c where c.id is null and p.status = :status", Proposta.class);
        query.setParameter("status", StatusProposta.ELEGIVEL);
        List<Proposta> resultList = query.getResultList();
        return resultList;
    }
}
