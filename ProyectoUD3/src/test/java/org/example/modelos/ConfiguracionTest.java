package org.example.modelos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * PRUEBAS JUNIT - ConfiguracionTest
 * @Test marca cada comprobacion automatica.
 * assertEquals compara el resultado con lo esperado y assertNotNull comprueba
 * que una busqueda ha encontrado el objeto. Los tests DAO usan MariaDB real.
 * finally intenta borrar los registros creados durante las pruebas.
 */
public class ConfiguracionTest {
    // Prueba inicial: comprueba que se ejecuta JUnit 5.
    // No cuenta como prueba del proyecto; crearemos esas mas adelante.
    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void comprobarJUnit() {
        assertEquals(2, 1 + 1);
    }
}
