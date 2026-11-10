package org.example.dao;

/*
 * DAO GENERICO - BASADO EN EL DAO DE LA PRACTICA 4.01
 * Esta clase contiene las operaciones comunes: crear, modificar y eliminar.
 * EntityManager permite hablar con Hibernate; las transacciones agrupan
 * los cambios para confirmarlos (commit) o deshacerlos (rollback).
 * Mantenemos el nombre dao en minusculas, como en el proyecto entregado.
 */

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// T es el tipo de objeto (Proveedor, Pieza, Proyecto o Gestion).
// K es el tipo de su clave primaria (String o Integer).
// AutoCloseable permite usar try (ProveedorDao dao = new ProveedorDao()).
public abstract class dao<T, K> implements AutoCloseable {

    // La fabrica abre EntityManager para los DAO. Se crea una sola vez.
    private static final EntityManagerFactory fabrica = Persistence.createEntityManagerFactory("default");

    // Cada DAO trabaja con su propio EntityManager (sesion con Hibernate).
    protected EntityManager em = fabrica.createEntityManager();

    // LOGGER
    private static final Logger logger = LoggerFactory.getLogger(dao.class);

    public EntityManager getEntityManager() {
        return em;
    }

    public abstract T find(K id);

    // CREAR
    public T create(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            // Comienza la transaccion: aun no se han confirmado los cambios.
            transaccion.begin();
            em.persist(objeto);
            // Confirma la operacion en la base de datos.
            transaccion.commit();

            // GUARDAR MENSAJE
            logger.info("Registro añadido: {}", objeto.getClass().getSimpleName());

            return objeto;

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                // Si hay un fallo, deshago los cambios pendientes.
                transaccion.rollback();
            }

            // GUARDAR ERROR
            logger.error("Error al añadir registro", e);

            throw e;
        }
    }

    // MODIFICAR
    public T update(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            // Comienza la transaccion: aun no se han confirmado los cambios.
            transaccion.begin();

            T actualizado = em.merge(objeto);

            // Confirma la operacion en la base de datos.
            transaccion.commit();

            // GUARDAR MENSAJE
            logger.info("Registro modificado: {}", objeto.getClass().getSimpleName());

            return actualizado;

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                // Si hay un fallo, deshago los cambios pendientes.
                transaccion.rollback();
            }

            // GUARDAR ERROR
            logger.error("Error al modificar registro", e);

            throw e;
        }
    }

    // ELIMINAR
    public void delete(T objeto) {

        EntityTransaction transaccion = em.getTransaction();

        try {
            // Comienza la transaccion: aun no se han confirmado los cambios.
            transaccion.begin();

            // remove() necesita una entidad gestionada por este EntityManager.
            T entidad;
            if (em.contains(objeto)) {
                entidad = objeto;
            } else {
                entidad = em.merge(objeto);
            }

            em.remove(entidad);

            // Confirma la operacion en la base de datos.
            transaccion.commit();

            // GUARDAR MENSAJE
            logger.info("Registro eliminado: {}", objeto.getClass().getSimpleName());

        } catch (RuntimeException e) {

            if (transaccion.isActive()) {
                // Si hay un fallo, deshago los cambios pendientes.
                transaccion.rollback();
            }

            // GUARDAR ERROR
            logger.error("Error al eliminar registro", e);

            throw e;
        }
    }

    // CERRAR ENTITY MANAGER
    @Override
    public void close() {

        if (em.isOpen()) {
            em.close();
        }
    }
}
