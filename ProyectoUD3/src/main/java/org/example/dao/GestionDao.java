package org.example.dao;

import org.example.Idao.IGestionDao;
import org.example.modelos.Gestion;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class GestionDao extends dao<Gestion, Integer> implements IGestionDao {

    // BUSCAR POR ID
    @Override
    public Gestion find(Integer id) {

        return em.find(Gestion.class, id);
    }

    // CREAR
    @Override
    public Gestion create(Gestion gestion) {

        gestion.validar();
        return super.create(gestion);
    }

    // MODIFICAR
    @Override
    public Gestion update(Gestion gestion) {

        gestion.validar();
        return super.update(gestion);
    }

    // LISTAR TODAS LAS GESTIONES
    @Override
    public List<Gestion> listar() {

        String jpql = "SELECT g FROM Gestion g ORDER BY g.fecha";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        return consulta.getResultList();
    }

    // BUSCAR POR PROVEEDOR
    @Override
    public List<Gestion> buscarPorProveedor(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.proveedor.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }

    // BUSCAR POR PIEZA
    @Override
    public List<Gestion> buscarPorPieza(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.pieza.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }

    // BUSCAR POR PROYECTO
    @Override
    public List<Gestion> buscarPorProyecto(String codigo) {

        String jpql = "SELECT g FROM Gestion g WHERE g.proyecto.codigo = :codigo";

        TypedQuery<Gestion> consulta = em.createQuery(jpql, Gestion.class);

        consulta.setParameter("codigo", codigo);

        return consulta.getResultList();
    }
}
