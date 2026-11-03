package org.example.Idao;

import org.example.modelos.Pieza;

import java.util.List;

public interface IPiezaDao {

    List<Pieza> listar();

    List<Pieza> buscarPorNombre(String nombre);
}
