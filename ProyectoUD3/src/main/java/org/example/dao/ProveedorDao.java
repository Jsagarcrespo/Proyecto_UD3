package org.example.dao;

/*
 * DAO DE PROVEEDORES
 * Hereda las operaciones generales de dao y anade las consultas JPQL.
 * TypedQuery devuelve objetos Proveedor, no filas SQL sueltas.
 */

import org.example.Idao.IProveedorDao;
import org.example.modelos.Proveedor;

import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProveedorDao extends dao<Proveedor, String> implements IProveedorDao {

    // BUSCAR POR CODIGO
    // find() busca por clave primaria y devuelve null si no existe.
    @Override
    public Proveedor find(String codigo) {

        return em.find(Proveedor.class, codigo);
    }

    // CREAR
    // Antes de guardar validamos el objeto; super.create() realiza persist y commit.
    @Override
    public Proveedor create(Proveedor proveedor) {

        proveedor.validar();

        return super.create(proveedor);
    }

    // MODIFICAR
    // Valido y dejo que el DAO generico haga merge y confirme los cambios.
    @Override
    public Proveedor update(Proveedor proveedor) {

        proveedor.validar();

        return super.update(proveedor);
    }

    // LISTAR TODOS
    // SELECT FROM devuelve objetos de la entidad, ordenados para mostrarlos.
    @Override
    public List<Proveedor> listar() {

        String jpql = "SELECT p FROM Proveedor p ORDER BY p.nombre";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        return consulta.getResultList();
    }

    // BUSCAR POR NOMBRE
    // LIKE permite encontrar coincidencias aunque no escribamos el nombre completo.
    @Override
    public List<Proveedor> buscarPorNombre(String nombre) {

        String jpql = "SELECT p FROM Proveedor p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("nombre", "%" + nombre.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR APELLIDOS
    // Busco un apellido mediante JPQL, ignorando mayusculas y minusculas.
    @Override
    public List<Proveedor> buscarPorApellidos(String apellidos) {

        String jpql = "SELECT p FROM Proveedor p WHERE LOWER(p.apellidos) LIKE LOWER(:apellidos)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("apellidos", "%" + apellidos.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR DIRECCION
    // Busco parte de la direccion usando LIKE y un parametro.
    @Override
    public List<Proveedor> buscarPorDireccion(String direccion) {

        String jpql = "SELECT p FROM Proveedor p WHERE LOWER(p.direccion) LIKE LOWER(:direccion)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("direccion", "%" + direccion.trim() + "%");

        return consulta.getResultList();
    }

    // BUSCAR POR PIEZA SUMINISTRADA
    // JOIN une la entidad con sus gestiones para consultar piezas relacionadas.
    @Override
    public List<Proveedor> buscarPorPieza(String pieza) {

        String jpql = "SELECT DISTINCT p FROM Proveedor p JOIN p.gestiones g WHERE LOWER(g.pieza.nombre) LIKE LOWER(:pieza)";

        TypedQuery<Proveedor> consulta = em.createQuery(jpql, Proveedor.class);

        consulta.setParameter("pieza", "%" + pieza.trim() + "%");

        return consulta.getResultList();
    }

    // ELIMINAR
    // Primero cuento las gestiones relacionadas con JPQL; no borro si hay alguna.
    @Override
    public void delete(Proveedor proveedor) {

        String jpql = "SELECT COUNT(g) FROM Gestion g WHERE g.proveedor.codigo = :codigo";

        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class);

        consulta.setParameter("codigo", proveedor.getCodigo());

        Long total = consulta.getSingleResult();

        if (total > 0) {

            throw new IllegalArgumentException("No puedes eliminar un proveedor con suministros");
        }

        super.delete(proveedor);
    }
}
