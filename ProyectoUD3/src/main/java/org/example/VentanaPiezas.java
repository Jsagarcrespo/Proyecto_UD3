package org.example;

import org.example.dao.PiezaDao;
import org.example.modelos.Pieza;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentanaPiezas extends JFrame {

    private JPanel panelPrincipal;
    private JTextField Codigo;
    private JTextField Nombre;
    private JTextField Precio;
    private JTextField Descripcion;
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

    private List<Pieza> piezas = new ArrayList<>();

    public VentanaPiezas() {

        setTitle("Gestion de Piezas");
        setContentPane(panelPrincipal);
        setSize(780, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Lista.setEditable(false);
        TipoBusqueda.setModel(new DefaultComboBoxModel<>(new String[]{"Codigo", "Nombre"}));

        mostrarTodos();

        // BOTONES
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadirPieza();
            }
        });

        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarPieza();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPieza();
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
                buscarPieza();
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

        // AL PULSAR UNA LINEA, CARGAMOS LOS DATOS
        Lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int posicion = Lista.viewToModel2D(e.getPoint());
                    int linea = Lista.getLineOfOffset(posicion);

                    if (linea >= 0 && linea < piezas.size()) {
                        rellenarCampos(piezas.get(linea));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VentanaPiezas.this, "No se ha podido seleccionar la pieza");
                }
            }
        });
    }

    // RECOGER LOS DATOS DE LAS CAJAS DE TEXTO
    private Pieza leerPieza() {

        String codigo = Codigo.getText().trim();
        String nombre = Nombre.getText().trim();
        String descripcion = Descripcion.getText().trim();
        String textoPrecio = Precio.getText().trim().replace(",", ".");

        if (textoPrecio.isEmpty()) {
            throw new IllegalArgumentException("Introduce el precio");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un numero valido");
        }

        if (precio.scale() > 2) {
            throw new IllegalArgumentException("El precio puede tener como maximo 2 decimales");
        }

        Pieza pieza = new Pieza(codigo, nombre, precio, descripcion);
        pieza.validar();
        return pieza;
    }

    // AÑADIR
    private void anadirPieza() {
        try (PiezaDao dao = new PiezaDao()) {
            Pieza pieza = leerPieza();

            if (dao.find(pieza.getCodigo()) != null) {
                JOptionPane.showMessageDialog(this, "Ese codigo ya existe");
                return;
            }

            dao.create(pieza);
            JOptionPane.showMessageDialog(this, "Pieza añadida");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al añadir: " + e.getMessage());
        }
    }

    // MODIFICAR
    private void modificarPieza() {
        try (PiezaDao dao = new PiezaDao()) {
            Pieza datos = leerPieza();
            Pieza pieza = dao.find(datos.getCodigo());

            if (pieza == null) {
                JOptionPane.showMessageDialog(this, "La pieza no existe");
                return;
            }

            pieza.setNombre(datos.getNombre());
            pieza.setPrecio(datos.getPrecio());
            pieza.setDescripcion(datos.getDescripcion());
            dao.update(pieza);

            JOptionPane.showMessageDialog(this, "Pieza modificada");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    // ELIMINAR
    private void eliminarPieza() {
        String codigo = Codigo.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una pieza");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar esta pieza?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try (PiezaDao dao = new PiezaDao()) {
            Pieza pieza = dao.find(codigo);

            if (pieza == null) {
                JOptionPane.showMessageDialog(this, "La pieza no existe");
                return;
            }

            dao.delete(pieza);
            JOptionPane.showMessageDialog(this, "Pieza eliminada");
            limpiarCampos();
            mostrarTodos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    // BUSCAR POR CODIGO O NOMBRE
    private void buscarPieza() {
        String texto = Buscar.getText().trim();

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (PiezaDao dao = new PiezaDao()) {
            List<Pieza> resultados = new ArrayList<>();
            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {
                Pieza pieza = dao.find(texto);
                if (pieza != null) {
                    resultados.add(pieza);
                }
            } else if (opcion == 1) {
                resultados = dao.buscarPorNombre(texto);
            }

            mostrarLista(resultados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    // MOSTRAR TODAS
    private void mostrarTodos() {
        try (PiezaDao dao = new PiezaDao()) {
            mostrarLista(dao.listar());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + e.getMessage());
        }
    }

    // MOSTRAR LISTADO
    private void mostrarLista(List<Pieza> lista) {
        piezas = new ArrayList<>(lista);
        String texto = "";

        for (Pieza pieza : piezas) {
            texto += pieza.getCodigo() + " | " + pieza.getNombre() + " | " + pieza.getPrecio() + " € | " + pieza.getDescripcion() + "\n";
        }

        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    // CARGAR LA PIEZA SELECCIONADA
    private void rellenarCampos(Pieza pieza) {
        Codigo.setText(pieza.getCodigo());
        Nombre.setText(pieza.getNombre());
        Precio.setText(pieza.getPrecio().toString());
        Descripcion.setText(pieza.getDescripcion() == null ? "" : pieza.getDescripcion());
        Codigo.setEditable(false);
    }

    // LIMPIAR
    private void limpiarCampos() {
        Codigo.setText("");
        Nombre.setText("");
        Precio.setText("");
        Descripcion.setText("");
        Buscar.setText("");
        Codigo.setEditable(true);
        TipoBusqueda.setSelectedIndex(0);
    }
}
