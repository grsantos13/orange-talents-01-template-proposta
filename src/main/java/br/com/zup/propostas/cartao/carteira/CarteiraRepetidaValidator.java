package br.com.zup.propostas.cartao.carteira;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class CarteiraRepetidaValidator {
    @PersistenceContext
    private EntityManager manager;

    public boolean carteiraExistente(NovaCarteiraRequest request) {
        Query query = manager.createQuery(
                "select 1 from Carteira c where c.emissor = :emissor");
        query.setParameter("emissor", request.getCarteira());
        List<?> resultList = query.getResultList();

        Assert.isTrue(resultList.size() <= 1,
                "Encontrada mais de uma proposta para o documento " + request.getCarteira());

        return resultList.isEmpty();
    }
}
