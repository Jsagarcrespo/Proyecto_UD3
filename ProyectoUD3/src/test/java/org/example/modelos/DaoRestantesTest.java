package org.example;

import org.example.dao.*;
import org.example.modelos.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DaoRestantesTest {

    // PRUEBA 1: PIEZAS
    @Test
    void probarPieza() {

        String codigo = "Z" + UUID.randomUUID().toString().substring(0, 8);

        try {

            // AÑADIR
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = new Pieza(codigo, "Pieza Test", new BigDecimal("10.50"), "Descripcion de prueba");

                dao.create(pieza);
            }

            // BUSCAR Y MODIFICAR
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codigo);

                assertNotNull(pieza);
                assertEquals("Pieza Test", pieza.getNombre());

                pieza.setPrecio(new BigDecimal("20.50"));

                dao.update(pieza);
            }

            // COMPROBAR LOS CAMBIOS
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codigo);

                assertNotNull(pieza);

                assertEquals(0, pieza.getPrecio().compareTo(new BigDecimal("20.50")));

                assertFalse(dao.buscarPorNombre("Pieza Test").isEmpty());
            }

        } finally {

            // ELIMINAR DATOS DE PRUEBA
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codigo);

                if (pieza != null) {
                    dao.delete(pieza);
                }
            }
        }

        // COMPROBAR QUE YA NO EXISTE
        try (PiezaDao dao = new PiezaDao()) {

            assertNull(dao.find(codigo));
        }
    }


    // PRUEBA 2: PROYECTOS
    @Test
    void probarProyecto() {

        String codigo = "Y" + UUID.randomUUID().toString().substring(0, 8);

        try {

            // AÑADIR
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = new Proyecto(codigo, "Proyecto Test", "Vitoria");

                dao.create(proyecto);
            }

            // BUSCAR Y MODIFICAR
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codigo);

                assertNotNull(proyecto);
                assertEquals("Vitoria", proyecto.getCiudad());

                proyecto.setCiudad("Bilbao");

                dao.update(proyecto);
            }

            // COMPROBAR CAMBIOS Y BUSQUEDA
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codigo);

                assertNotNull(proyecto);

                assertEquals("Bilbao", proyecto.getCiudad());

                assertFalse(dao.buscarPorCiudad("Bilbao").isEmpty());
            }

        } finally {

            // ELIMINAR DATOS DE PRUEBA
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codigo);

                if (proyecto != null) {
                    dao.delete(proyecto);
                }
            }
        }

        // COMPROBAR QUE YA NO EXISTE
        try (ProyectoDao dao = new ProyectoDao()) {

            assertNull(dao.find(codigo));
        }
    }


    // PRUEBA 3: GESTIONES Y RELACIONES
    @Test
    void probarGestion() {

        String codigo = UUID.randomUUID().toString().substring(0, 8);

        String codigoProveedor = "P" + codigo;
        String codigoPieza = "Z" + codigo;
        String codigoProyecto = "Y" + codigo;

        Integer idGestion = null;

        try {

            // CREAR PROVEEDOR
            try (ProveedorDao dao = new ProveedorDao()) {

                dao.create(new Proveedor(codigoProveedor, "Proveedor Test", "Apellido Test", "Calle Test"));
            }

            // CREAR PIEZA
            try (PiezaDao dao = new PiezaDao()) {

                dao.create(new Pieza(codigoPieza, "Pieza Test", new BigDecimal("12.50"), "Descripcion Test"));
            }

            // CREAR PROYECTO
            try (ProyectoDao dao = new ProyectoDao()) {

                dao.create(new Proyecto(codigoProyecto, "Proyecto Test", "Vitoria"));
            }

            // CREAR GESTION
            try (GestionDao dao = new GestionDao()) {

                Proveedor proveedor = dao.getEntityManager().find(Proveedor.class, codigoProveedor);

                Pieza pieza = dao.getEntityManager().find(Pieza.class, codigoPieza);

                Proyecto proyecto = dao.getEntityManager().find(Proyecto.class, codigoProyecto);

                Gestion gestion = new Gestion(LocalDate.now(), 10, proveedor, pieza, proyecto);

                dao.create(gestion);

                idGestion = gestion.getId();

                assertNotNull(idGestion);
            }

            // MODIFICAR CANTIDAD
            try (GestionDao dao = new GestionDao()) {

                Gestion gestion = dao.find(idGestion);

                assertNotNull(gestion);

                gestion.setCantidad(25);

                dao.update(gestion);
            }

            // COMPROBAR LA MODIFICACION Y BUSQUEDAS
            try (GestionDao dao = new GestionDao()) {

                Gestion gestion = dao.find(idGestion);

                assertNotNull(gestion);

                assertEquals(25, gestion.getCantidad());

                assertFalse(dao.buscarPorProveedor(codigoProveedor).isEmpty());

                assertFalse(dao.buscarPorPieza(codigoPieza).isEmpty());

                assertFalse(dao.buscarPorProyecto(codigoProyecto).isEmpty());
            }

            // NO PERMITIR BORRAR PIEZA CON SUMINISTROS
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codigoPieza);

                assertThrows(IllegalArgumentException.class, () -> dao.delete(pieza));
            }

            // NO PERMITIR BORRAR PROYECTO CON SUMINISTROS
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codigoProyecto);

                assertThrows(IllegalArgumentException.class, () -> dao.delete(proyecto));
            }

        } finally {

            // PRIMERO BORRAR LA GESTION
            if (idGestion != null) {

                try (GestionDao dao = new GestionDao()) {

                    Gestion gestion = dao.find(idGestion);

                    if (gestion != null) {
                        dao.delete(gestion);
                    }
                }
            }

            // BORRAR PROVEEDOR
            try (ProveedorDao dao = new ProveedorDao()) {

                Proveedor proveedor = dao.find(codigoProveedor);

                if (proveedor != null) {
                    dao.delete(proveedor);
                }
            }

            // BORRAR PIEZA
            try (PiezaDao dao = new PiezaDao()) {

                Pieza pieza = dao.find(codigoPieza);

                if (pieza != null) {
                    dao.delete(pieza);
                }
            }

            // BORRAR PROYECTO
            try (ProyectoDao dao = new ProyectoDao()) {

                Proyecto proyecto = dao.find(codigoProyecto);

                if (proyecto != null) {
                    dao.delete(proyecto);
                }
            }
        }
    }
}
