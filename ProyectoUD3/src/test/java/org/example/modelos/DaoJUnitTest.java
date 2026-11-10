package org.example;

import org.example.dao.*;
import org.example.modelos.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DaoJUnitTest {

    // PRUEBA 1: PROVEEDORES
    @Test
    void probarProveedor() {

        String codigo = "T" + UUID.randomUUID().toString().substring(0, 8);

        try {

            // AÑADIR
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = new Proveedor(codigo, "Proveedor Test", "Apellido Test", "Calle A");

                dao.create(proveedor);
            }

            // BUSCAR Y MODIFICAR
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codigo);

                assertNotNull(proveedor);

                assertEquals("Proveedor Test", proveedor.getNombre());

                proveedor.setDireccion("Calle B");

                dao.update(proveedor);
            }

            // COMPROBAR MODIFICACION
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codigo);

                assertNotNull(proveedor);

                assertEquals("Calle B", proveedor.getDireccion());
            }

        } finally {

            // ELIMINAR DATOS DE PRUEBA
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codigo);

                if (proveedor != null) {
                    dao.delete(proveedor);
                }
            }
        }

        // COMPROBAR ELIMINACION
        try (ProveedorDao dao = new ProveedorDao()) {

            assertNull(dao.find(codigo));
        }
    }


    // PRUEBA 2: GESTIONES
    @Test
    void probarGestion() {

        String codigo = UUID.randomUUID().toString().substring(0, 8);

        String codProveedor = "P" + codigo;
        String codPieza = "Z" + codigo;
        String codProyecto = "Y" + codigo;

        Integer idGestion = null;

        try {

            // CREAR PROVEEDOR
            try (ProveedorDao dao = new ProveedorDao()) {

                dao.create(new Proveedor(codProveedor, "Proveedor Test", "Apellido Test", "Calle A"));
            }

            // CREAR PIEZA
            try (PiezaDao dao = new PiezaDao()) {

                dao.create(new Pieza(codPieza, "Tornillos", new BigDecimal("5.50"), "Caja de tornillos"));
            }

            // CREAR PROYECTO
            try (ProyectoDao dao = new ProyectoDao()) {

                dao.create(new Proyecto(codProyecto, "Proyecto Test", "Vitoria"));
            }

            // CREAR GESTION
            try (GestionDao dao = new GestionDao()) {

                Proveedor proveedor = dao.getEntityManager().find(Proveedor.class, codProveedor);

                Pieza pieza = dao.getEntityManager().find(Pieza.class, codPieza);

                Proyecto proyecto = dao.getEntityManager().find(Proyecto.class, codProyecto);

                Gestion gestion = new Gestion(LocalDate.now(), 100, proveedor, pieza, proyecto);

                dao.create(gestion);

                idGestion = gestion.getId();

                assertNotNull(idGestion);
            }

            // COMPROBAR QUE EXISTE
            try (GestionDao dao = new GestionDao()) {

                Gestion gestion = dao.find(idGestion);

                assertNotNull(gestion);

                assertEquals(100, gestion.getCantidad());
            }

            // COMPROBAR QUE NO SE PUEDE
            // ELIMINAR UN PROVEEDOR CON SUMINISTROS
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codProveedor);

                assertThrows(IllegalArgumentException.class, () -> dao.delete(proveedor));
            }

        } finally {

            // ELIMINAR PRIMERO LA GESTION
            if (idGestion != null) {

                try (GestionDao dao = new GestionDao()) {

                    Gestion gestion = dao.find(idGestion);

                    if (gestion != null) {
                        dao.delete(gestion);
                    }
                }
            }

            // ELIMINAR PROVEEDOR
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codProveedor);

                if (proveedor != null) {
                    dao.delete(proveedor);
                }
            }

            // ELIMINAR PIEZA
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codPieza);

                if (pieza != null) {
                    dao.delete(pieza);
                }
            }

            // ELIMINAR PROYECTO
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codProyecto);

                if (proyecto != null) {
                    dao.delete(proyecto);
                }
            }
        }
    }
}
