package org.example.dao;

import org.example.Idao.IProveedorDao;
import org.example.modelos.Proveedor;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProveedorDao extends dao<Proveedor, String> implements IProveedorDao {

    // busca pro codigo
    @Override
    public Proveedor find(String codigo) {

        return em.find(Proveedor.class, codigo);
    }

    // crear un proveedor
    @Override
    public Proveedor create(Proveedor proveedor) {

        proveedor.validar();
        return super.create(proveedor);
    }

    // mod un proveedor
    @Override
    public Proveedor update(Proveedor proveedor) {

        proveedor.validar();
        return super.update(proveedor);
    }

    //lista todos los proveedores
    @Override
    public List<Proveedor> listar() {

        String jpql = "SELECT p FROM Proveedor p ORDER BY p.nombre";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        return consulta.getResultList();
    }

    // buscar por nombre
    @Override
    public List<Proveedor> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Proveedor p " + "WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // buscar por dir
    @Override
    public List<Proveedor> buscarPorDireccion(String direccion) {

        String jpql = "SELECT p FROM Proveedor p " + "WHERE LOWER(p.direccion) LIKE LOWER(:direccion)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("direccion", "%" + direccion.trim() + "%");

        return consulta.getResultList();
    }


    // ELIMINAR
    @Override
    public void delete(Proveedor proveedor) {

        String jpql = "SELECT COUNT(g) FROM Gestion g " + "WHERE g.proveedor.codigo = :codigo";

        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);

        consulta.setParameter("codigo", proveedor.getCodigo());

        Long total = consulta.getSingleResult();

        if (total > 0) {
            throw new IllegalArgumentException("No puedes eliminar un proveedor con suministros");
        }

        super.delete(proveedor);
    }

}
