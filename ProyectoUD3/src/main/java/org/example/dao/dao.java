package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public abstract class dao<T, K> implements AutoCloseable {

    private static final EntityManagerFactory fabrica = Persistence.createEntityManagerFactory("default");

    protected EntityManager em = fabrica.createEntityManager();

    public EntityManager getEntityManager() {
        return em;
    }

    public abstract T find(K id);

    // CREAR
    public T create(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();
            em.persist(objeto);
            transaccion.commit();

            return objeto;

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                transaccion.rollback();
            }

            throw e;
        }
    }

    // MODIFICAR
    public T update(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();
            T actualizado = em.merge(objeto);
            transaccion.commit();

            return actualizado;

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                transaccion.rollback();
            }

            throw e;
        }
    }

    // ELIMINAR
    public void delete(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();

            T entidad = em.contains(objeto) ? objeto : em.merge(objeto);

            em.remove(entidad);
            transaccion.commit();

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                transaccion.rollback();
            }

            throw e;
        }
    }

    @Override
    public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
