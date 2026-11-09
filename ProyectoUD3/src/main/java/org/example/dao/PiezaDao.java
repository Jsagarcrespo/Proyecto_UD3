package org.example.dao;

import org.example.Idao.IPiezaDao;
import org.example.modelos.Pieza;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class PiezaDao extends dao<Pieza, String> implements IPiezaDao {

    // BUSCAR POR CODIGO
    @Override
    public Pieza find(String codigo) {

        return em.find(Pieza.class, codigo);
    }

    // CREAR
    @Override
    public Pieza create(Pieza pieza) {

        pieza.validar();
        return super.create(pieza);
    }

    // MODIFICAR
    @Override
    public Pieza update(Pieza pieza) {

        pieza.validar();
        return super.update(pieza);
    }

    // LISTAR TODAS
    @Override
    public List<Pieza> listar() {

        String jpql = "SELECT p FROM Pieza p ORDER BY p.nombre";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        return consulta.getResultList();
    }

    // BUSCAR POR NOMBRE
    @Override
    public List<Pieza> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Pieza p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Pieza> consulta = em.createQuery(jpql, Pieza.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // ELIMINAR
    @Override
    public void delete(Pieza pieza) {

        // Comprobar si tiene suministros asociados
        String jpql = "SELECT COUNT(g) FROM Gestion g WHERE g.pieza.codigo = :codigo";

        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);

        consulta.setParameter("codigo", pieza.getCodigo());

        Long total = consulta.getSingleResult();

        if (total > 0) {
            throw new IllegalArgumentException("No puedes eliminar una pieza con suministros");
        }

        super.delete(pieza);
    }
}
