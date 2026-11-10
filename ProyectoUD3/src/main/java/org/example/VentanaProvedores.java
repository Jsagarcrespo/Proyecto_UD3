package org.example;

/*
 * VENTANA DE PROVEEDORES
 * Los JTextField recogen datos y el JTextArea muestra la lista.
 * Los botones llaman a metodos de esta clase, como en la UD2.
 * ProveedorDao se encarga de consultar y guardar con Hibernate.
 */

import org.example.dao.ProveedorDao;
import org.example.modelos.Proveedor;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaProvedores extends JFrame {

    private JPanel panelPrincipal;
    private JTextField Codigo;
    private JTextField Nombre;
    private JTextField Apellidos;
    private JTextField Direccion;
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

    // El constructor prepara la ventana y conecta los botones con los metodos.
    // Cada ActionListener se ejecuta cuando el usuario pulsa su boton.
    public VentanaProvedores() {

        setTitle("Gestion de Proveedores");
        setContentPane(panelPrincipal);
        setSize(780, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Lista.setEditable(false);
        Lista.setLineWrap(false); // Una linea del listado corresponde a un registro.

        TipoBusqueda.setModel(new DefaultComboBoxModel<>(new String[]{"Codigo", "Nombre", "Apellidos", "Direccion", "Pieza suministrada"}));

        mostrarTodos();

        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadirProveedor();
            }
        });

        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarProveedor();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProveedor();
            }
        });

        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProveedor();
            }
        });

        mostrarTodosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarTodos();
            }
        });

        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Al pulsar en una linea, se rellenan los campos.
        Lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // No seleccionar el ultimo registro si pulsamos debajo del listado.
                    int altoLinea = Lista.getFontMetrics(Lista.getFont()).getHeight();
                    if (e.getY() >= altoLinea * proveedores.size()) {
                        return;
                    }

                    int posicion = Lista.viewToModel2D(e.getPoint());
                    int linea = Lista.getLineOfOffset(posicion);
                    if (linea >= 0 && linea < proveedores.size()) {
                        rellenarCampos(proveedores.get(linea));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaProvedores.this, "No se ha podido seleccionar el proveedor");
                }
            }
        });
    }

    // Lee los cuatro JTextField, crea un Proveedor temporal y valida
    // que no falten campos antes de guardar nada en MariaDB.
    private Proveedor leerProveedor() {
        Proveedor proveedor = new Proveedor(Codigo.getText().trim(), Nombre.getText().trim(), Apellidos.getText().trim(), Direccion.getText().trim());
        proveedor.validar();
        return proveedor;
    }

    // Primero compruebo que ese codigo no exista; despues hago dao.create().
    // El try/catch muestra el error al usuario sin cerrar la ventana.
    private void anadirProveedor() {
        // try (...) cierra el EntityManager cuando termina la operacion,
        // incluso si se produce un error que capturamos en catch.
        try (ProveedorDao dao = new ProveedorDao()) {
            Proveedor proveedor = leerProveedor();
            if (dao.find(proveedor.getCodigo()) != null) {
                JOptionPane.showMessageDialog(this, "Ese codigo ya existe");
                return;
            }
            dao.create(proveedor);
            JOptionPane.showMessageDialog(this, "Proveedor añadido");
            limpiarCampos();
            mostrarTodos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al añadir: " + e.getMessage());
        }
    }

    // Busco el Proveedor original con find() y cambio solo sus datos.
    // No cambio el codigo ni sus relaciones con las gestiones.
    private void modificarProveedor() {
        try (ProveedorDao dao = new ProveedorDao()) {
            // Leo los datos escritos por el usuario.
            Proveedor datos = leerProveedor();

            // Busco el proveedor que ya esta guardado.
            // Asi solo modifico los campos editables, sin tocar sus suministros.
            Proveedor proveedor = dao.find(datos.getCodigo());
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this, "El proveedor no existe");
                return;
            }

            proveedor.setNombre(datos.getNombre());
            proveedor.setApellidos(datos.getApellidos());
            proveedor.setDireccion(datos.getDireccion());
            dao.update(proveedor);
            JOptionPane.showMessageDialog(this, "Proveedor modificado");
            limpiarCampos();
            mostrarTodos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    // Pido confirmacion antes de borrar. ProveedorDao revisa que
    // no haya suministros asociados y, si existen, impide el borrado.
    private void eliminarProveedor() {
        String codigo = Codigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try (ProveedorDao dao = new ProveedorDao()) {
            Proveedor proveedor = dao.find(codigo);
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this, "El proveedor no existe");
                return;
            }
            dao.delete(proveedor);
            JOptionPane.showMessageDialog(this, "Proveedor eliminado");
            limpiarCampos();
            mostrarTodos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }


    // BUSCAR PROVEEDORES
    // Segun el JComboBox, elijo una busqueda distinta en el DAO.
    // La busqueda por pieza usa JOIN, porque consulta las gestiones.
    private void buscarProveedor() {

        String texto = Buscar.getText().trim();

        // Una busqueda nueva invalida la seleccion anterior, aunque
        // el texto introducido sea incorrecto o la busqueda falle.
        Codigo.setText("");
        Codigo.setEditable(true);

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (ProveedorDao dao = new ProveedorDao()) {

            List<Proveedor> resultados = new ArrayList<>();

            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {

                // BUSCAR POR CODIGO
                Proveedor proveedor = dao.find(texto);

                if (proveedor != null) {
                    resultados.add(proveedor);
                }

            } else if (opcion == 1) {

                // BUSCAR POR NOMBRE
                resultados = dao.buscarPorNombre(texto);

            } else if (opcion == 2) {

                // BUSCAR POR APELLIDOS
                resultados = dao.buscarPorApellidos(texto);

            } else if (opcion == 3) {

                // BUSCAR POR DIRECCION
                resultados = dao.buscarPorDireccion(texto);

            } else if (opcion == 4) {

                // BUSCAR POR PIEZA SUMINISTRADA
                resultados = dao.buscarPorPieza(texto);
            }

            mostrarLista(resultados);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }


    private void mostrarTodos() {
        try (ProveedorDao dao = new ProveedorDao()) {
            mostrarLista(dao.listar());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    // Guarda el resultado en una lista y muestra cada objeto en una linea.
    // La lista tambien sirve para saber que objeto se ha pulsado con el raton.
    private void mostrarLista(List<Proveedor> lista) {

        // Al cambiar el listado, borro los datos del registro anterior.
        // De esta manera no se modifica ni se elimina uno por accidente.
        Codigo.setText("");
        Nombre.setText("");
        Apellidos.setText("");
        Direccion.setText("");
        Codigo.setEditable(true);

        proveedores = new ArrayList<>(lista);
        String texto = "";
        for (Proveedor p : proveedores) {
            texto += p.getCodigo() + " | " + p.getNombre() + " | " + p.getApellidos() + " | " + p.getDireccion() + "\n";
        }
        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    // Copia al formulario los datos del registro seleccionado para editarlo.
    private void rellenarCampos(Proveedor proveedor) {
        Codigo.setText(proveedor.getCodigo());
        Nombre.setText(proveedor.getNombre());
        Apellidos.setText(proveedor.getApellidos());
        Direccion.setText(proveedor.getDireccion());
        Codigo.setEditable(false);
    }

    // Deja el formulario preparado para introducir otro registro.
    private void limpiarCampos() {
        Codigo.setText("");
        Nombre.setText("");
        Apellidos.setText("");
        Direccion.setText("");
        Buscar.setText("");
        Codigo.setEditable(true);
        TipoBusqueda.setSelectedIndex(0);
    }
}
