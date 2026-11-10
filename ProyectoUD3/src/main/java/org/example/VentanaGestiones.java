package org.example;

/*
 * VENTANA DE SUMINISTROS (GESTIONES)
 * Cada gestion relaciona un proveedor, una pieza y un proyecto.
 * Los tres JComboBox muestran registros que ya existen en la BD.
 * El ID se genera en MariaDB/Hibernate, por eso no se escribe a mano.
 */

import org.example.dao.GestionDao;
import org.example.dao.PiezaDao;
import org.example.dao.ProveedorDao;
import org.example.dao.ProyectoDao;
import org.example.modelos.Gestion;
import org.example.modelos.Pieza;
import org.example.modelos.Proveedor;
import org.example.modelos.Proyecto;

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

    // El constructor prepara la ventana y conecta los botones con los metodos.
    // Cada ActionListener se ejecuta cuando el usuario pulsa su boton.
    public VentanaGestiones() {

        setTitle("Gestion de Suministros");
        setContentPane(panelPrincipal);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        IdGestion.setEditable(false);
        Lista.setEditable(false);
        Lista.setLineWrap(false); // Cada suministro se muestra en una sola linea.
        Fecha.setText(LocalDate.now().toString());

        TipoBusqueda.setModel(new DefaultComboBoxModel<>(new String[]{"ID", "Proveedor", "Pieza", "Proyecto"}));

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
                    // El espacio en blanco debajo de las gestiones no es un registro.
                    int altoLinea = Lista.getFontMetrics(Lista.getFont()).getHeight();
                    if (e.getY() >= altoLinea * gestiones.size()) {
                        return;
                    }
                    int posicion = Lista.viewToModel2D(e.getPoint());
                    int linea = Lista.getLineOfOffset(posicion);

                    if (linea >= 0 && linea < gestiones.size()) {
                        rellenarCampos(gestiones.get(linea));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaGestiones.this, "Error al seleccionar suministro");
                }
            }
        });
    }

    // CARGAR LOS PROVEEDORES, PIEZAS Y PROYECTOS EN LOS COMBOBOX
    // Llamo a listar() de los tres DAO y relleno los JComboBox.
    // La opcion de indice 0 es un texto de ayuda, no un registro real.
    private void cargarCombos() {

        // try (...) cierra el EntityManager cuando termina la operacion,
        // incluso si se produce un error que capturamos en catch.
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
    // Recojo fecha y cantidad, y compruebo los tres desplegables.
    // Con EntityManager.find() recupero las entidades de ESTA sesion
    // antes de crear la Gestion que vamos a guardar.
    private Gestion leerGestion(GestionDao dao) {

        if (ComboProveedores.getSelectedIndex() <= 0 || ComboPiezas.getSelectedIndex() <= 0 || ComboProyectos.getSelectedIndex() <= 0) {
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

        // Restamos 1 porque el indice 0 contiene "Selecciona un...".
        // Las listas Java empiezan en indice 0, pero los datos del combo en 1.
        Proveedor seleccionadoProveedor = proveedores.get(ComboProveedores.getSelectedIndex() - 1);
        Pieza seleccionadaPieza = piezas.get(ComboPiezas.getSelectedIndex() - 1);
        Proyecto seleccionadoProyecto = proyectos.get(ComboProyectos.getSelectedIndex() - 1);

        // Recuperar las entidades en el mismo EntityManager que la gestion
        Proveedor proveedor = dao.getEntityManager().find(Proveedor.class, seleccionadoProveedor.getCodigo());
        Pieza pieza = dao.getEntityManager().find(Pieza.class, seleccionadaPieza.getCodigo());
        Proyecto proyecto = dao.getEntityManager().find(Proyecto.class, seleccionadoProyecto.getCodigo());

        // Si se ha borrado alguno de estos registros desde otra ventana,
        // find() devolvera null. Avisamos para que se pulse Mostrar todos.
        if (proveedor == null || pieza == null || proyecto == null) {
            throw new IllegalArgumentException(
                    "Falta un proveedor, una pieza o un proyecto. Pulsa Mostrar todos");
        }

        Gestion gestion = new Gestion(fecha, cantidad, proveedor, pieza, proyecto);
        gestion.validar();
        return gestion;
    }

    // AÑADIR
    // Creo una Gestion nueva; Hibernate asignara automaticamente su ID.
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
    // Busco la Gestion por su ID, leo los datos y actualizo sus campos.
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
    // Confirmo y elimino la Gestion. Despues se pueden borrar sus padres.
    private void eliminarGestion() {

        if (IdGestion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un suministro del listado");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar este suministro?", "Eliminar", JOptionPane.YES_NO_OPTION);

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
    // Busca una gestion por su ID o por el codigo de alguna entidad asociada.
    private void buscarGestion() {

        String texto = Buscar.getText().trim();

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        // Si la nueva busqueda falla, no conservamos el ID del suministro anterior.
        IdGestion.setText("");

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
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se han encontrado suministros");
            }

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

    // MOSTRAR LOS SUMINISTROS EN PANTALLA
    // Guarda el resultado en una lista y muestra cada objeto en una linea.
    // La lista tambien sirve para saber que objeto se ha pulsado con el raton.
    private void mostrarLista(List<Gestion> lista) {

        // Quitar la seleccion anterior
        IdGestion.setText("");
        Fecha.setText(LocalDate.now().toString());
        Cantidad.setText("");

        // Tras un error de conexion, un combo podria no tener elementos.
        if (ComboProveedores.getItemCount() > 0) ComboProveedores.setSelectedIndex(0);
        if (ComboPiezas.getItemCount() > 0) ComboPiezas.setSelectedIndex(0);
        if (ComboProyectos.getItemCount() > 0) ComboProyectos.setSelectedIndex(0);

        gestiones = new ArrayList<>(lista);

        String texto = "";

        for (Gestion gestion : gestiones) {

            texto += "ID: " + gestion.getId() + " | " + gestion.getFecha() + " | " + gestion.getCantidad() + " unidades" + " | Proveedor: " + gestion.getProveedor().getCodigo() + " | Pieza: " + gestion.getPieza().getCodigo() + " | Proyecto: " + gestion.getProyecto().getCodigo() + "\n";
        }

        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }


    // RELLENAR LOS CAMPOS AL ELEGIR UNA LINEA
    // Copia al formulario los datos del registro seleccionado para editarlo.
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
    // Deja el formulario preparado para introducir otro registro.
    private void limpiarCampos() {

        IdGestion.setText("");
        Fecha.setText(LocalDate.now().toString());
        Cantidad.setText("");
        Buscar.setText("");

        // Tras un error de conexion, un combo podria no tener elementos.
        if (ComboProveedores.getItemCount() > 0) ComboProveedores.setSelectedIndex(0);
        if (ComboPiezas.getItemCount() > 0) ComboPiezas.setSelectedIndex(0);
        if (ComboProyectos.getItemCount() > 0) ComboProyectos.setSelectedIndex(0);
        if (TipoBusqueda.getItemCount() > 0) TipoBusqueda.setSelectedIndex(0);
    }
}
