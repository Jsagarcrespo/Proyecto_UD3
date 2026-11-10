package org.example.modelos;

import org.example.dao.ProveedorDao;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/*
 * PRUEBAS JUNIT - ProveedorDAOTest
 * @Test marca cada comprobacion automatica.
 * assertEquals compara el resultado con lo esperado y assertNotNull comprueba
 * que una busqueda ha encontrado el objeto. Los tests DAO usan MariaDB real.
 * finally intenta borrar los registros creados durante las pruebas.
 */
public class ProveedorDAOTest {

    // JUnit ejecuta este metodo y marca en rojo la prueba si falla un assert.
    @Test
    void comprobarCrudProveedor() {

        String codigo = "T" + UUID.randomUUID().toString().substring(0, 12);

        String nombre = "Prueba" + codigo;

        try (ProveedorDao dao = new ProveedorDao()) {

            try {
                // CREAR
                Proveedor proveedor = new Proveedor(codigo, nombre, "Apellido Prueba", "Calle Prueba");

                dao.create(proveedor);

                // BUSCAR
                Proveedor encontrado = dao.find(codigo);

                assertNotNull(encontrado);
                assertEquals(nombre, encontrado.getNombre());

                // MODIFICAR
                proveedor.setNombre("Modificado" + codigo);

                dao.update(proveedor);

                assertEquals("Modificado" + codigo, dao.find(codigo).getNombre());

                // BUSCAR POR NOMBRE
                assertEquals(1, dao.buscarPorNombre("Modificado" + codigo).size());

                // ELIMINAR
                dao.delete(dao.find(codigo));

                assertNull(dao.find(codigo));

            } finally {
                // Limpiar los datos de la prueba
                Proveedor guardado = dao.find(codigo);

                if (guardado != null) {
                    dao.delete(guardado);
                }
            }
        }
    }
}

