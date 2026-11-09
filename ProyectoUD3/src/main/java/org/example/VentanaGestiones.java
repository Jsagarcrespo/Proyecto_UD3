package org.example;

import org.example.dao.GestionDao;
import org.example.dao.PiezaDao;
import org.example.dao.ProveedorDao;
import org.example.dao.ProyectoDao;
import org.example.models.Gestion;
import org.example.models.Pieza;
import org.example.models.Proveedor;
import org.example.models.Proyecto;

import javax.swing.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VentanaGestiones extends JFrame {

    private JPanel panelPrincipal;
    private JTextField IdGestion;
    private JTextField Fecha;
    private JTextField Cantidad;
    private JComboBox<String> ComboProveedores;
    private JComboBox<String> ComboPiezas;
    private JComboBox<String> ComboProyectos;
    private JTextField Buscar;
    private JComboBox<String> TipoBusqueda;
    private JTextArea Lista;

    private JButton anadirButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton buscarButton;
    private JButton mostrarTodosButton;
    private JButton cerrarButton;

    private List<Proveedor> proveedores = new ArrayList<>();
    private List<Pieza> piezas = new ArrayList<>();
    private List<Proyecto> proyectos = new ArrayList<>();
    private List<Gestion> gestiones = new ArrayList<>();

    public VentanaGestiones() {

        setTitle("Gestion de Suministros");
        setContentPane(panelPrincipal);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        IdGestion.setEditable(false);
        Lista.setEditable(false);
        Fecha.setText(LocalDate.now().toString());

        TipoBusqueda.setModel(new DefaultComboBoxModel<>(
                new String[]{"ID", "Proveedor", "Pieza", "Proyecto"}));

        mostrarTodos();

        // AÑADIR
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadirGestion();
            }
        });

        // MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarGestion();
            }
        });

        // ELIMINAR
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarGestion();
            }
        });

        // LIMPIAR
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        // BUSCAR
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarGestion();
            }
        });

        // MOSTRAR TODOS Y ACTUALIZAR DESPLEGABLES
        mostrarTodosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarTodos();
            }
        });

        // CERRAR
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // SELECCIONAR UN SUMINISTRO DEL LISTADO
        Lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int posicion = Lista.viewToModel2D(e.getPoint());
                    int linea = Lista.getLineOfOffset(posicion);

                    if (linea >= 0 && linea < gestiones.size()) {
                        rellenarCampos(gestiones.get(linea));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaGestiones.this,
                            "Error al seleccionar suministro");
                }
            }
        });
    }

    // CARGAR LOS PROVEEDORES, PIEZAS Y PROYECTOS EN LOS COMBOBOX
    private void cargarCombos() {

        try (ProveedorDao dao = new ProveedorDao()) {
            proveedores = dao.listar();
        }
        try (PiezaDao dao = new PiezaDao()) {
            piezas = dao.listar();
        }
        try (ProyectoDao dao = new ProyectoDao()) {
            proyectos = dao.listar();
        }

        ComboProveedores.removeAllItems();
        ComboProveedores.addItem("Selecciona un proveedor");
        for (Proveedor proveedor : proveedores) {
            ComboProveedores.addItem(proveedor.getCodigo() + " - " + proveedor.getNombre());
        }

        ComboPiezas.removeAllItems();
        ComboPiezas.addItem("Selecciona una pieza");
        for (Pieza pieza : piezas) {
            ComboPiezas.addItem(pieza.getCodigo() + " - " + pieza.getNombre());
        }

        ComboProyectos.removeAllItems();
        ComboProyectos.addItem("Selecciona un proyecto");
        for (Proyecto proyecto : proyectos) {
            ComboProyectos.addItem(proyecto.getCodigo() + " - " + proyecto.getNombre());
        }
    }

    // LEER LOS DATOS DEL FORMULARIO
    private Gestion leerGestion(GestionDao dao) {

        if (ComboProveedores.getSelectedIndex() <= 0 ||
                ComboPiezas.getSelectedIndex() <= 0 ||
                ComboProyectos.getSelectedIndex() <= 0) {
            throw new IllegalArgumentException("Debes elegir proveedor, pieza y proyecto");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(Fecha.getText().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato AAAA-MM-DD");
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(Cantidad.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser un numero entero");
        }

        Proveedor seleccionadoProveedor = proveedores.get(ComboProveedores.getSelectedIndex() - 1);
        Pieza seleccionadaPieza = piezas.get(ComboPiezas.getSelectedIndex() - 1);
        Proyecto seleccionadoProyecto = proyectos.get(ComboProyectos.getSelectedIndex() - 1);

        // Recuperar las entidades en el mismo EntityManager que la gestion
        Proveedor proveedor = dao.getEntityManager().find(Proveedor.class, seleccionadoProveedor.getCodigo());
        Pieza pieza = dao.getEntityManager().find(Pieza.class, seleccionadaPieza.getCodigo());
        Proyecto proyecto = dao.getEntityManager().find(Proyecto.class, seleccionadoProyecto.getCodigo());

        Gestion gestion = new Gestion(fecha, cantidad, proveedor, pieza, proyecto);
        gestion.validar();
        return gestion;
    }

    // AÑADIR
    private void anadirGestion() {

        try (GestionDao dao = new GestionDao()) {
            Gestion gestion = leerGestion(dao);
            dao.create(gestion);

            JOptionPane.showMessageDialog(this, "Suministro añadido correctamente");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al añadir: " + e.getMessage());
        }
    }

    // MODIFICAR
    private void modificarGestion() {

        if (IdGestion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un suministro del listado");
            return;
        }

        try (GestionDao dao = new GestionDao()) {
            int id = Integer.parseInt(IdGestion.getText());
            Gestion gestion = dao.find(id);

            if (gestion == null) {
                JOptionPane.showMessageDialog(this, "El suministro no existe");
                return;
            }

            Gestion datos = leerGestion(dao);
            gestion.setFecha(datos.getFecha());
            gestion.setCantidad(datos.getCantidad());
            gestion.setProveedor(datos.getProveedor());
            gestion.setPieza(datos.getPieza());
            gestion.setProyecto(datos.getProyecto());

            dao.update(gestion);

            JOptionPane.showMessageDialog(this, "Suministro modificado correctamente");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    // ELIMINAR
    private void eliminarGestion() {

        if (IdGestion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un suministro del listado");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Quieres eliminar este suministro?", "Eliminar", JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try (GestionDao dao = new GestionDao()) {
            int id = Integer.parseInt(IdGestion.getText());
            Gestion gestion = dao.find(id);

            if (gestion == null) {
                JOptionPane.showMessageDialog(this, "El suministro no existe");
                return;
            }

            dao.delete(gestion);

            JOptionPane.showMessageDialog(this, "Suministro eliminado correctamente");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    // BUSCAR POR ID O POR CODIGO DE LAS ENTIDADES
    private void buscarGestion() {

        String texto = Buscar.getText().trim();

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (GestionDao dao = new GestionDao()) {
            List<Gestion> resultados = new ArrayList<>();
            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {
                int id = Integer.parseInt(texto);
                Gestion gestion = dao.find(id);
                if (gestion != null) {
                    resultados.add(gestion);
                }
            } else if (opcion == 1) {
                resultados = dao.buscarPorProveedor(texto);
            } else if (opcion == 2) {
                resultados = dao.buscarPorPieza(texto);
            } else if (opcion == 3) {
                resultados = dao.buscarPorProyecto(texto);
            }

            mostrarLista(resultados);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un numero entero");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    // MOSTRAR TODOS LOS SUMINISTROS
    private void mostrarTodos() {

        try {
            cargarCombos();

            try (GestionDao dao = new GestionDao()) {
                mostrarLista(dao.listar());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    // ESCRIBIR LA LISTA EN PANTALLA
    private void mostrarLista(List<Gestion> lista) {

        gestiones = new ArrayList<>(lista);
        String texto = "";

        for (Gestion gestion : gestiones) {
            texto += "ID: " + gestion.getId()
                    + " | " + gestion.getFecha()
                    + " | " + gestion.getCantidad() + " unidades"
                    + " | Proveedor: " + gestion.getProveedor().getCodigo()
                    + " | Pieza: " + gestion.getPieza().getCodigo()
                    + " | Proyecto: " + gestion.getProyecto().getCodigo() + "\n";
        }

        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    // RELLENAR LOS CAMPOS AL ELEGIR UNA LINEA
    private void rellenarCampos(Gestion gestion) {

        IdGestion.setText(String.valueOf(gestion.getId()));
        Fecha.setText(gestion.getFecha().toString());
        Cantidad.setText(String.valueOf(gestion.getCantidad()));

        for (int i = 0; i < proveedores.size(); i++) {
            if (proveedores.get(i).getCodigo().equals(gestion.getProveedor().getCodigo())) {
                ComboProveedores.setSelectedIndex(i + 1);
                break;
            }
        }

        for (int i = 0; i < piezas.size(); i++) {
            if (piezas.get(i).getCodigo().equals(gestion.getPieza().getCodigo())) {
                ComboPiezas.setSelectedIndex(i + 1);
                break;
            }
        }

        for (int i = 0; i < proyectos.size(); i++) {
            if (proyectos.get(i).getCodigo().equals(gestion.getProyecto().getCodigo())) {
                ComboProyectos.setSelectedIndex(i + 1);
                break;
            }
        }
    }

    // LIMPIAR
    private void limpiarCampos() {

        IdGestion.setText("");
        Fecha.setText(LocalDate.now().toString());
        Cantidad.setText("");
        Buscar.setText("");

        ComboProveedores.setSelectedIndex(0);
        ComboPiezas.setSelectedIndex(0);
        ComboProyectos.setSelectedIndex(0);
        TipoBusqueda.setSelectedIndex(0);
    }
}
