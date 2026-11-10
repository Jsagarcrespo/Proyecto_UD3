package org.example.dao;

/*
 * DAO DE GESTIONES
 * Guarda los suministros y permite buscarlos por proveedor, pieza o proyecto.
 * En JPQL escribimos los nombres de las clases y de sus atributos Java.
 */

import org.example.Idao.IGestionDao;
import org.example.modelos.Gestion;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class GestionDao extends dao<Gestion, Integer> implements IGestionDao {

    // BUSCAR POR ID
    // find() busca por clave primaria y devuelve null si no existe.
    @Override
    public Gestion find(Integer id) {

        return em.find(Gestion.class, id);
    }

    // CREAR
    // Antes de guardar validamos el objeto; super.create() realiza persist y commit.
    @Override
    public Gestion create(Gestion gestion) {

        gestion.validar();
        return super.create(gestion);
    }

    // MODIFICAR
    // Valido y dejo que el DAO generico haga merge y confirme los cambios.
    @Override
    public Gestion update(Gestion gestion) {

        gestion.validar();
        return super.update(gestion);
    }

    // LISTAR TODAS LAS GESTIONES
    // SELECT FROM devuelve objetos de la entidad, ordenados para mostrarlos.
    @Override
    public List<Gestion> listar() {

        String jpql = "SELECT g FROM Gestion g ORDER BY g.fecha";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        return consulta.getResultList();
    }

    // BUSCAR POR PROVEEDOR
    // La consulta utiliza la relacion con Gestion para filtrar por proveedor.
    @Override
    public List<Gestion> buscarPorProveedor(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.proveedor.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }

    // BUSCAR POR PIEZA
    // JOIN une la entidad con sus gestiones para consultar piezas relacionadas.
    @Override
    public List<Gestion> buscarPorPieza(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.pieza.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }

    // BUSCAR POR PROYECTO
    // La consulta utiliza la relacion con Gestion para filtrar por proyecto.
    @Override
    public List<Gestion> buscarPorProyecto(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.proyecto.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }
}
