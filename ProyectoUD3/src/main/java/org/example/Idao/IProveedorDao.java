package org.example.Idao;

import org.example.modelos.Proveedor;

import java.util.List;

public interface IProveedorDao {

    List<Proveedor> listar();

    List<Proveedor> buscarPorNombre(String nombre);

    List<Proveedor> buscarPorApellidos(String apellidos);

    List<Proveedor> buscarPorDireccion(String direccion);

    List<Proveedor> buscarPorPieza(String pieza);
}
