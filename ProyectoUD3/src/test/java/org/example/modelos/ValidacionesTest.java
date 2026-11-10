package org.example.modelos;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/*
 * PRUEBAS JUNIT - ValidacionesTest
 * @Test marca cada comprobacion automatica.
 * assertEquals compara el resultado con lo esperado y assertNotNull comprueba
 * que una busqueda ha encontrado el objeto. Los tests DAO usan MariaDB real.
 * finally intenta borrar los registros creados durante las pruebas.
 */
public class ValidacionesTest {

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void proveedorCorrecto() {
        Proveedor proveedor = new Proveedor("PR01", "Proveedor 1", "Apellidos 1", "Calle A");
        assertDoesNotThrow(proveedor::validar);
    }

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void proveedorSinCodigo() {
        Proveedor proveedor = new Proveedor("", "Proveedor 1", "Apellidos 1", "Calle A");
        assertThrows(IllegalArgumentException.class, proveedor::validar);
    }

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void piezaConPrecioNegativo() {
        Pieza pieza = new Pieza("P01", "Pieza A", new BigDecimal("-2.50"), "Descripcion");
        assertThrows(IllegalArgumentException.class, pieza::validar);
    }

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void proyectoSinCiudad() {
        Proyecto proyecto = new Proyecto("PY01", "Proyecto A", "");
        assertThrows(IllegalArgumentException.class, proyecto::validar);
    }

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void gestionConCantidadCero() {
        Proveedor proveedor = new Proveedor("PR01", "Proveedor 1", "Apellidos 1", "Calle A");
        Pieza pieza = new Pieza("P01", "Pieza A", new BigDecimal("12.50"), "Descripcion");
        Proyecto proyecto = new Proyecto("PY01", "Proyecto A", "Vitoria");
        Gestion gestion = new Gestion(LocalDate.now(), 0, proveedor, pieza, proyecto);
        assertThrows(IllegalArgumentException.class, gestion::validar);
    }

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void gestionCorrecta() {
        Proveedor proveedor = new Proveedor("PR01", "Proveedor 1", "Apellidos 1", "Calle A");
        Pieza pieza = new Pieza("P01", "Pieza A", new BigDecimal("12.50"), "Descripcion");
        Proyecto proyecto = new Proyecto("PY01", "Proyecto A", "Vitoria");
        Gestion gestion = new Gestion(LocalDate.now(), 5, proveedor, pieza, proyecto);
        assertDoesNotThrow(gestion::validar);
    }
}
