package org.example.Idao;

import org.example.modelos.Gestion;

import java.util.List;

public interface IGestionDao {

    List<Gestion> listar();

    List<Gestion> buscarPorProveedor(String codigo);

    List<Gestion> buscarPorPieza(String codigo);

    List<Gestion> buscarPorProyecto(String codigo);
}
