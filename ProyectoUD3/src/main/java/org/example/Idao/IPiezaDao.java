package org.example.Idao;

import org.example.modelos.Pieza;

import java.math.BigDecimal;
import java.util.List;

// La interfaz indica que consultas debe tener el DAO de piezas.
// No realiza consultas; solo declara los metodos que implementa la clase DAO.
public interface IPiezaDao {

    List<Pieza> listar();

    List<Pieza> buscarPorNombre(String nombre);

    List<Pieza> buscarPorPrecio(BigDecimal minimo, BigDecimal maximo);

    List<Pieza> buscarPorProveedor(String codigo);

    List<Pieza> buscarPorProyecto(String codigo);
}
