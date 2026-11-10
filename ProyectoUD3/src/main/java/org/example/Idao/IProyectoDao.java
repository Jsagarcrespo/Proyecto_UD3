package org.example.Idao;

import org.example.modelos.Proyecto;

import java.util.List;

// La interfaz indica que consultas debe tener el DAO de proyectos.
// No realiza consultas; solo declara los metodos que implementa la clase DAO.
public interface IProyectoDao {

    List<Proyecto> listar();

    List<Proyecto> buscarPorNombre(String nombre);

    List<Proyecto> buscarPorCiudad(String ciudad);

    List<Proyecto> buscarPorProveedor(String codigo);

    List<Proyecto> buscarPorPieza(String codigo);
}
