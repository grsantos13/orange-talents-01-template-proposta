package br.com.zup.propostas.proposta;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class TransactionExecutor {

    @PersistenceContext
    private EntityManager manager;

    public <T> T persist(T entity){
        manager.persist(entity);
        return entity;
    }

    public <T> T merge(T entity){
        manager.merge(entity);
        return entity;
    }
}
