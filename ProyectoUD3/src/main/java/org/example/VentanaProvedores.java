package org.example;

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

    public VentanaProvedores() {

        setTitle("Gestion de Proveedores");
        setContentPane(panelPrincipal);
        setSize(780, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Lista.setEditable(false);
        TipoBusqueda.setModel(new DefaultComboBoxModel<>(
                new String[]{"Codigo", "Nombre", "Direccion"}));

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
                    int posicion = Lista.viewToModel2D(e.getPoint());
                    int linea = Lista.getLineOfOffset(posicion);
                    if (linea >= 0 && linea < proveedores.size()) {
                        rellenarCampos(proveedores.get(linea));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaProvedores.this,
                            "No se ha podido seleccionar el proveedor");
                }
            }
        });
    }

    private Proveedor leerProveedor() {
        Proveedor proveedor = new Proveedor(
                Codigo.getText().trim(),
                Nombre.getText().trim(),
                Apellidos.getText().trim(),
                Direccion.getText().trim()
        );
        proveedor.validar();
        return proveedor;
    }

    private void anadirProveedor() {
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

    private void modificarProveedor() {
        try (ProveedorDao dao = new ProveedorDao()) {
            Proveedor proveedor = leerProveedor();
            if (dao.find(proveedor.getCodigo()) == null) {
                JOptionPane.showMessageDialog(this, "El proveedor no existe");
                return;
            }
            dao.update(proveedor);
            JOptionPane.showMessageDialog(this, "Proveedor modificado");
            limpiarCampos();
            mostrarTodos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        String codigo = Codigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
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

    private void buscarProveedor() {
        String texto = Buscar.getText().trim();
        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (ProveedorDao dao = new ProveedorDao()) {
            List<Proveedor> resultados = new ArrayList<>();
            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {
                Proveedor proveedor = dao.find(texto);
                if (proveedor != null) {
                    resultados.add(proveedor);
                }
            } else if (opcion == 1) {
                resultados = dao.buscarPorNombre(texto);
            } else if (opcion == 2) {
                resultados = dao.buscarPorDireccion(texto);
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

    private void mostrarLista(List<Proveedor> lista) {
        proveedores = new ArrayList<>(lista);
        String texto = "";
        for (Proveedor p : proveedores) {
            texto += p.getCodigo() + " | " + p.getNombre() + " | "
                    + p.getApellidos() + " | " + p.getDireccion() + "\n";
        }
        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    private void rellenarCampos(Proveedor proveedor) {
        Codigo.setText(proveedor.getCodigo());
        Nombre.setText(proveedor.getNombre());
        Apellidos.setText(proveedor.getApellidos());
        Direccion.setText(proveedor.getDireccion());
        Codigo.setEditable(false);
    }

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
