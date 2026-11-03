
package org.example.Idao;

import org.example.modelos.Proyecto;

import java.util.List;

public interface IProyectoDao {

    List<Proyecto> listar();

    List<Proyecto> buscarPorNombre(String nombre);

    List<Proyecto> buscarPorCiudad(String ciudad);
}
