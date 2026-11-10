package org.example.Idao;

import org.example.modelos.Proveedor;

import java.util.List;

// La interfaz indica que consultas debe tener el DAO de proveedores.
// No realiza consultas; solo declara los metodos que implementa la clase DAO.
public interface IProveedorDao {

    List<Proveedor> listar();

    List<Proveedor> buscarPorNombre(String nombre);

    List<Proveedor> buscarPorApellidos(String apellidos);

    List<Proveedor> buscarPorDireccion(String direccion);

    List<Proveedor> buscarPorPieza(String pieza);
}
