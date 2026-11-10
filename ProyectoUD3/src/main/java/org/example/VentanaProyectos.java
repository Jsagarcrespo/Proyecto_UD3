package org.example;

/*
 * VENTANA DE PROYECTOS
 * Hace las mismas operaciones que la de proveedores, pero sobre Proyecto.
 * Utiliza JTextField, JComboBox, JTextArea y botones Swing.
 * Las consultas JPQL estan en ProyectoDao.
 */

import org.example.dao.ProyectoDao;
import org.example.modelos.Proyecto;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaProyectos extends JFrame {

    private JPanel panelPrincipal;

    private JTextField Codigo;
    private JTextField Nombre;
    private JTextField Ciudad;
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

    private List<Proyecto> proyectos = new ArrayList<>();

    // El constructor prepara la ventana y conecta los botones con los metodos.
    // Cada ActionListener se ejecuta cuando el usuario pulsa su boton.
    public VentanaProyectos() {

        setTitle("Gestion de Proyectos");
        setContentPane(panelPrincipal);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Lista.setEditable(false);
        Lista.setLineWrap(false); // Una linea del listado corresponde a un registro.


        TipoBusqueda.setModel(new DefaultComboBoxModel<>(new String[]{"Codigo", "Nombre", "Ciudad", "Proveedor (codigo)", "Pieza (codigo)"}));


        mostrarTodos();

        // AÑADIR
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadirProyecto();
            }
        });

        // MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarProyecto();
            }
        });

        // ELIMINAR
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProyecto();
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
                buscarProyecto();
            }
        });

        // MOSTRAR TODOS
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

        // SELECCIONAR PROYECTO DEL LISTADO
        Lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    // El area vacia no debe seleccionar por error el ultimo proyecto.
                    int altoLinea = Lista.getFontMetrics(Lista.getFont()).getHeight();
                    if (e.getY() >= altoLinea * proyectos.size()) {
                        return;
                    }


                    int posicion = Lista.viewToModel2D(e.getPoint());

                    int linea = Lista.getLineOfOffset(posicion);

                    if (linea >= 0 && linea < proyectos.size()) {

                        rellenarCampos(proyectos.get(linea));
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(VentanaProyectos.this, "Error al seleccionar el proyecto");
                }
            }
        });
    }

    // LEER LOS DATOS DEL FORMULARIO
    // Creo un Proyecto con los tres JTextField y reviso que sea valido.
    private Proyecto leerProyecto() {

        String codigo = Codigo.getText().trim();
        String nombre = Nombre.getText().trim();
        String ciudad = Ciudad.getText().trim();

        Proyecto proyecto = new Proyecto(codigo, nombre, ciudad);

        proyecto.validar();

        return proyecto;
    }

    // AÑADIR PROYECTO
    // Evito codigos repetidos con find() antes de hacer create().
    private void anadirProyecto() {

        // try (...) cierra el EntityManager cuando termina la operacion,
        // incluso si se produce un error que capturamos en catch.
        try (ProyectoDao dao = new ProyectoDao()) {

            Proyecto proyecto = leerProyecto();

            if (dao.find(proyecto.getCodigo()) != null) {

                JOptionPane.showMessageDialog(this, "Ya existe un proyecto con ese codigo");
                return;
            }

            dao.create(proyecto);

            JOptionPane.showMessageDialog(this, "Proyecto añadido correctamente");

            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al añadir: " + e.getMessage());
        }
    }

    // MODIFICAR PROYECTO
    // Solo cambio nombre y ciudad de un proyecto que ya existe.
    private void modificarProyecto() {

        try (ProyectoDao dao = new ProyectoDao()) {

            Proyecto datos = leerProyecto();

            Proyecto proyecto = dao.find(datos.getCodigo());

            if (proyecto == null) {

                JOptionPane.showMessageDialog(this, "El proyecto no existe");
                return;
            }

            proyecto.setNombre(datos.getNombre());
            proyecto.setCiudad(datos.getCiudad());

            dao.update(proyecto);

            JOptionPane.showMessageDialog(this, "Proyecto modificado correctamente");

            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    // ELIMINAR PROYECTO
    // El DAO comprueba antes de eliminar que no tenga suministros.
    private void eliminarProyecto() {

        String codigo = Codigo.getText().trim();

        if (codigo.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Selecciona un proyecto");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar este proyecto?", "Eliminar", JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try (ProyectoDao dao = new ProyectoDao()) {

            Proyecto proyecto = dao.find(codigo);

            if (proyecto == null) {

                JOptionPane.showMessageDialog(this, "El proyecto no existe");
                return;
            }

            dao.delete(proyecto);

            JOptionPane.showMessageDialog(this, "Proyecto eliminado correctamente");

            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    // BUSCAR PROYECTOS
    // La opcion del JComboBox decide que consulta JPQL ejecutar.
    private void buscarProyecto() {

        String texto = Buscar.getText().trim();

        // Una busqueda nueva invalida la seleccion anterior, aunque
        // el texto introducido sea incorrecto o la busqueda falle.
        Codigo.setText("");
        Codigo.setEditable(true);

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (ProyectoDao dao = new ProyectoDao()) {

            List<Proyecto> resultados = new ArrayList<>();

            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {

                // BUSCAR POR CODIGO
                Proyecto proyecto = dao.find(texto);

                if (proyecto != null) {
                    resultados.add(proyecto);
                }

            } else if (opcion == 1) {

                // BUSCAR POR NOMBRE
                resultados = dao.buscarPorNombre(texto);

            } else if (opcion == 2) {

                // BUSCAR POR CIUDAD
                resultados = dao.buscarPorCiudad(texto);

            } else if (opcion == 3) {

                // BUSCAR POR PROVEEDOR
                resultados = dao.buscarPorProveedor(texto);

            } else if (opcion == 4) {

                // BUSCAR POR PIEZA
                resultados = dao.buscarPorPieza(texto);
            }

            mostrarLista(resultados);

            if (resultados.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No se han encontrado proyectos");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }


    // MOSTRAR TODOS LOS PROYECTOS
    private void mostrarTodos() {

        try (ProyectoDao dao = new ProyectoDao()) {

            mostrarLista(dao.listar());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    // MOSTRAR PROYECTOS EN LA LISTA
    // Guarda el resultado en una lista y muestra cada objeto en una linea.
    // La lista tambien sirve para saber que objeto se ha pulsado con el raton.
    private void mostrarLista(List<Proyecto> lista) {

        // Al cambiar el listado, borro los datos del registro anterior.
        // De esta manera no se modifica ni se elimina uno por accidente.
        Codigo.setText("");
        Nombre.setText("");
        Ciudad.setText("");
        Codigo.setEditable(true);


        proyectos = new ArrayList<>(lista);

        String texto = "";

        for (Proyecto proyecto : proyectos) {

            texto += proyecto.getCodigo() + " | " + proyecto.getNombre() + " | " + proyecto.getCiudad() + "\n";
        }

        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    // RELLENAR LOS CAMPOS
    // Copia al formulario los datos del registro seleccionado para editarlo.
    private void rellenarCampos(Proyecto proyecto) {

        Codigo.setText(proyecto.getCodigo());
        Nombre.setText(proyecto.getNombre());
        Ciudad.setText(proyecto.getCiudad());

        // No modificar el codigo al seleccionar
        Codigo.setEditable(false);
    }

    // LIMPIAR CAMPOS
    // Deja el formulario preparado para introducir otro registro.
    private void limpiarCampos() {

        Codigo.setText("");
        Nombre.setText("");
        Ciudad.setText("");
        Buscar.setText("");

        Codigo.setEditable(true);
        TipoBusqueda.setSelectedIndex(0);
    }
}
