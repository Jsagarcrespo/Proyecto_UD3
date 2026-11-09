package org.example.dao;

import org.example.Idao.IProyectoDao;
import org.example.modelos.Proyecto;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProyectoDao extends dao<Proyecto, String> implements IProyectoDao {

    // BUSCAR POR CODIGO
    @Override
    public Proyecto find(String codigo) {

        return em.find(Proyecto.class, codigo);
    }

    // CREAR
    @Override
    public Proyecto create(Proyecto proyecto) {

        proyecto.validar();
        return super.create(proyecto);
    }

    // MODIFICAR
    @Override
    public Proyecto update(Proyecto proyecto) {

        proyecto.validar();
        return super.update(proyecto);
    }

    // LISTAR TODOS
    @Override
    public List<Proyecto> listar() {

        String jpql = "SELECT p FROM Proyecto p ORDER BY p.nombre";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        return consulta.getResultList();
    }

    // BUSCAR POR NOMBRE
    @Override
    public List<Proyecto> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Proyecto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR CIUDAD
    @Override
    public List<Proyecto> buscarPorCiudad(String ciudad) {

        String jpql = "SELECT p FROM Proyecto p WHERE LOWER(p.ciudad) LIKE LOWER(:ciudad)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("ciudad", "%" + ciudad.trim() + "%");

        return consulta.getResultList();
    }

    // ELIMINAR
    @Override
    public void delete(Proyecto proyecto) {

        String jpql = "SELECT COUNT(g) FROM Gestion g WHERE g.proyecto.codigo = :codigo";

        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);

        consulta.setParameter("codigo", proyecto.getCodigo());

        Long total = consulta.getSingleResult();

        if (total > 0) {
            throw new IllegalArgumentException("No puedes eliminar un proyecto con suministros");
        }

        super.delete(proyecto);
    }
}
