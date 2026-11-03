package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        EntityManagerFactory fabrica = null;
        EntityManager em = null;

        try {
            fabrica = Persistence.createEntityManagerFactory("default");
            em = fabrica.createEntityManager();
            Object resultado = em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("Conexion a MariaDB correcta: " + resultado);
            logger.info("Conexion comprobada");
        } catch (Exception e) {
            System.out.println("No se pudo conectar: comprueba MariaDB, la base de datos y los datos de acceso.");
            logger.error("Error al iniciar la aplicacion", e);
        } finally {
            if (em != null && em.isOpen()) em.close();
            if (fabrica != null && fabrica.isOpen()) fabrica.close();
        }
    }
}
