package br.com.zup.propostas.proposta;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Component
public class ImpedeDocumentoIgualValidator {

    private EntityManager manager;

    public ImpedeDocumentoIgualValidator(EntityManager manager) {
        this.manager = manager;
    }

    public boolean documentoEstaValido(NovaPropostaRequest request) {
        Query query = manager.createQuery(
                "select 1 from Proposta p where p.documento = :documento");
        query.setParameter("documento", request.getDocumento());
        List<?> resultList = query.getResultList();

        Assert.isTrue(resultList.size() <= 1,
                "Encontrada mais de uma proposta para o documento " + request.getDocumento());

        return resultList.isEmpty();
    }
}
