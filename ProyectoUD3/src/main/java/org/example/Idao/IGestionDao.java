package org.example.Idao;

import org.example.modelos.Gestion;

import java.util.List;

// La interfaz indica que consultas debe tener el DAO de gestiones.
// No realiza consultas; solo declara los metodos que implementa la clase DAO.
public interface IGestionDao {

    List<Gestion> listar();

    List<Gestion> buscarPorProveedor(String codigo);

    List<Gestion> buscarPorPieza(String codigo);

    List<Gestion> buscarPorProyecto(String codigo);
}
