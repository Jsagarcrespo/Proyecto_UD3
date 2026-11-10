package org.example.dao;

/*
 * DAO DE PROYECTOS
 * Permite buscar por codigo, nombre, ciudad, proveedor y pieza.
 * Las relaciones se consultan mediante JOIN con la lista gestiones.
 */

import org.example.Idao.IProyectoDao;
import org.example.modelos.Proyecto;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProyectoDao extends dao<Proyecto, String> implements IProyectoDao {

    // BUSCAR POR CODIGO
    // find() busca por clave primaria y devuelve null si no existe.
    @Override
    public Proyecto find(String codigo) {

        return em.find(Proyecto.class, codigo);
    }

    // AÑADIR
    // Antes de guardar validamos el objeto; super.create() realiza persist y commit.
    @Override
    public Proyecto create(Proyecto proyecto) {

        proyecto.validar();

        return super.create(proyecto);
    }

    // MODIFICAR
    // Valido y dejo que el DAO generico haga merge y confirme los cambios.
    @Override
    public Proyecto update(Proyecto proyecto) {

        proyecto.validar();

        return super.update(proyecto);
    }

    // ELIMINAR
    // Primero cuento las gestiones relacionadas con JPQL; no borro si hay alguna.
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

    // MOSTRAR TODOS
    // SELECT FROM devuelve objetos de la entidad, ordenados para mostrarlos.
    @Override
    public List<Proyecto> listar() {

        String jpql = "SELECT p FROM Proyecto p ORDER BY p.nombre";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        return consulta.getResultList();
    }

    // BUSCAR POR NOMBRE
    // LIKE permite encontrar coincidencias aunque no escribamos el nombre completo.
    @Override
    public List<Proyecto> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Proyecto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR CIUDAD
    // LIKE permite buscar un proyecto escribiendo parte de la ciudad.
    @Override
    public List<Proyecto> buscarPorCiudad(String ciudad) {

        String jpql = "SELECT p FROM Proyecto p WHERE LOWER(p.ciudad) LIKE LOWER(:ciudad)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("ciudad", "%" + ciudad.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR PROYECTOS POR PROVEEDOR
    // La consulta utiliza la relacion con Gestion para filtrar por proveedor.
    @Override
    public List<Proyecto> buscarPorProveedor(String codigo) {

        String jpql = "SELECT DISTINCT p FROM Proyecto p JOIN p.gestiones g WHERE LOWER(g.proveedor.codigo) = LOWER(:codigo)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("codigo", codigo.trim());

        return consulta.getResultList();
    }

    // BUSCAR PROYECTOS POR PIEZA
    // JOIN une la entidad con sus gestiones para consultar piezas relacionadas.
    @Override
    public List<Proyecto> buscarPorPieza(String codigo) {

        String jpql = "SELECT DISTINCT p FROM Proyecto p JOIN p.gestiones g WHERE LOWER(g.pieza.codigo) = LOWER(:codigo)";

        TypedQuery<Proyecto> consulta = em.createQuery(jpql, Proyecto.class);

        consulta.setParameter("codigo", codigo.trim());

        return consulta.getResultList();
    }
}
