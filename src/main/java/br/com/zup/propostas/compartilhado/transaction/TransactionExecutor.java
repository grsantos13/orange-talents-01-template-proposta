package br.com.zup.propostas.compartilhado.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class TransactionExecutor {

    @PersistenceContext
    private EntityManager manager;

    public <T, I> T find(Class<T> entity, I id) {
        T response = manager.find(entity, id);
        if (response == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entity.getSimpleName() + " não encontrada para o id " + id);
        return response;
    }

    @Transactional
    public <T> T persist(T entity) {
        manager.persist(entity);
        return entity;
    }

    @Transactional
    public <T> T merge(T entity) {
        manager.merge(entity);
        return entity;
    }

    @Transactional
    public <T> void remove(T entity) {
        manager.remove(entity);
    }

}
