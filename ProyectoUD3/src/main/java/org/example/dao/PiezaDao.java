package org.example.dao;

/*
 * DAO DE PIEZAS
 * Utiliza JPQL para las busquedas y la clase dao para guardar cambios.
 * Antes de borrar una pieza comprueba si tiene suministros asociados.
 */

import org.example.Idao.IPiezaDao;
import org.example.modelos.Pieza;

import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

public class PiezaDao extends dao<Pieza, String> implements IPiezaDao {

    // BUSCAR POR CODIGO
    // find() busca por clave primaria y devuelve null si no existe.
    @Override
    public Pieza find(String codigo) {

        return em.find(Pieza.class, codigo);
    }

    // AÑADIR
    // Antes de guardar validamos el objeto; super.create() realiza persist y commit.
    @Override
    public Pieza create(Pieza pieza) {

        pieza.validar();

        return super.create(pieza);
    }

    // MODIFICAR
    // Valido y dejo que el DAO generico haga merge y confirme los cambios.
    @Override
    public Pieza update(Pieza pieza) {

        pieza.validar();

        return super.update(pieza);
    }

    // ELIMINAR
    // Primero cuento las gestiones relacionadas con JPQL; no borro si hay alguna.
    @Override
    public void delete(Pieza pieza) {

        String jpql = "SELECT COUNT(g) FROM Gestion g WHERE g.pieza.codigo = :codigo";

        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);

        consulta.setParameter("codigo", pieza.getCodigo());

        Long total = consulta.getSingleResult();

        if (total > 0) {

            throw new IllegalArgumentException("No puedes eliminar una pieza con suministros");
        }

        super.delete(pieza);
    }

    // MOSTRAR TODAS
    // SELECT FROM devuelve objetos de la entidad, ordenados para mostrarlos.
    @Override
    public List<Pieza> listar() {

        String jpql = "SELECT p FROM Pieza p ORDER BY p.nombre";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        return consulta.getResultList();
    }

    // BUSCAR POR NOMBRE
    // LIKE permite encontrar coincidencias aunque no escribamos el nombre completo.
    @Override
    public List<Pieza> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Pieza p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR PRECIO
    // BETWEEN filtra piezas entre dos precios, incluidos los limites.
    @Override
    public List<Pieza> buscarPorPrecio(BigDecimal minimo, BigDecimal maximo) {

        String jpql = "SELECT p FROM Pieza p WHERE p.precio BETWEEN :minimo AND :maximo ORDER BY p.precio";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        consulta.setParameter("minimo", minimo);
        consulta.setParameter("maximo", maximo);

        return consulta.getResultList();
    }

    // BUSCAR PIEZAS POR PROVEEDOR
    // La consulta utiliza la relacion con Gestion para filtrar por proveedor.
    @Override
    public List<Pieza> buscarPorProveedor(String codigo) {

        String jpql = "SELECT DISTINCT p FROM Pieza p JOIN p.gestiones g WHERE LOWER(g.proveedor.codigo) = LOWER(:codigo)";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        consulta.setParameter("codigo", codigo.trim());

        return consulta.getResultList();
    }

    // BUSCAR PIEZAS POR PROYECTO
    // La consulta utiliza la relacion con Gestion para filtrar por proyecto.
    @Override
    public List<Pieza> buscarPorProyecto(String codigo) {

        String jpql = "SELECT DISTINCT p FROM Pieza p JOIN p.gestiones g WHERE LOWER(g.proyecto.codigo) = LOWER(:codigo)";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        consulta.setParameter("codigo", codigo.trim());

        return consulta.getResultList();
    }
}
