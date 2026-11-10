package org.example;

/*
 * VENTANA DE PIEZAS
 * Permite anadir, modificar, eliminar, buscar y listar piezas.
 * El precio se lee como texto y se convierte a BigDecimal.
 * Los datos se guardan mediante PiezaDao; la ventana no escribe SQL.
 */

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

    // El constructor prepara la ventana y conecta los botones con los metodos.
    // Cada ActionListener se ejecuta cuando el usuario pulsa su boton.
    public VentanaPiezas() {

        setTitle("Gestion de Piezas");
        setContentPane(panelPrincipal);
        setSize(780, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Lista.setEditable(false);
        Lista.setLineWrap(false); // Una linea del listado corresponde a un registro.

        TipoBusqueda.setModel(new DefaultComboBoxModel<>(new String[]{"Codigo", "Nombre", "Precio entre", "Proveedor (codigo)", "Proyecto (codigo)"}));

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
                    // No seleccionar el ultimo registro si pulsamos debajo del listado.
                    int altoLinea = Lista.getFontMetrics(Lista.getFont()).getHeight();
                    if (e.getY() >= altoLinea * piezas.size()) {
                        return;
                    }

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
    // Convierto el precio introducido en un BigDecimal, no en double.
    // Asi trabajamos con importes decimales sin errores de redondeo binario.
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
    // Valido los campos, compruebo que el codigo sea nuevo y guardo.
    private void anadirPieza() {
        // try (...) cierra el EntityManager cuando termina la operacion,
        // incluso si se produce un error que capturamos en catch.
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
    // Recupero la pieza existente y actualizo nombre, precio y descripcion.
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
    // Compruebo que el usuario confirme y que la pieza no tenga gestiones.
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

    // BUSCAR PIEZAS
    // Permite buscar por codigo, nombre, intervalo de precios,
    // proveedor o proyecto. El intervalo se escribe por ejemplo 10;50.
    private void buscarPieza() {

        String texto = Buscar.getText().trim();

        // Una busqueda nueva invalida la seleccion anterior, aunque
        // el texto introducido sea incorrecto o la busqueda falle.
        Codigo.setText("");
        Codigo.setEditable(true);

        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }

        try (PiezaDao dao = new PiezaDao()) {

            List<Pieza> resultados = new ArrayList<>();

            int opcion = TipoBusqueda.getSelectedIndex();

            if (opcion == 0) {

                // BUSCAR POR CODIGO
                Pieza pieza = dao.find(texto);

                if (pieza != null) {
                    resultados.add(pieza);
                }

            } else if (opcion == 1) {

                // BUSCAR POR NOMBRE
                resultados = dao.buscarPorNombre(texto);

            } else if (opcion == 2) {

                // BUSCAR POR INTERVALO DE PRECIOS

                String[] precios = texto.split(";", -1);

                if (precios.length != 2) {

                    JOptionPane.showMessageDialog(this, "Escribe los precios asi: 10;50");
                    return;
                }

                BigDecimal minimo = new BigDecimal(precios[0].trim().replace(",", "."));

                BigDecimal maximo = new BigDecimal(precios[1].trim().replace(",", "."));

                if (minimo.compareTo(BigDecimal.ZERO) < 0 || maximo.compareTo(minimo) < 0) {

                    JOptionPane.showMessageDialog(this, "El intervalo de precios no es valido");
                    return;
                }

                resultados = dao.buscarPorPrecio(minimo, maximo);

            } else if (opcion == 3) {

                // BUSCAR POR CODIGO DE PROVEEDOR
                resultados = dao.buscarPorProveedor(texto);

            } else if (opcion == 4) {

                // BUSCAR POR CODIGO DE PROYECTO
                resultados = dao.buscarPorProyecto(texto);
            }

            mostrarLista(resultados);

            if (resultados.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No se han encontrado piezas");
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this, "Precio incorrecto. Ejemplo: 10;50");

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
    // Guarda el resultado en una lista y muestra cada objeto en una linea.
    // La lista tambien sirve para saber que objeto se ha pulsado con el raton.
    private void mostrarLista(List<Pieza> lista) {

        // Al cambiar el listado, borro los datos del registro anterior.
        // De esta manera no se modifica ni se elimina uno por accidente.
        Codigo.setText("");
        Nombre.setText("");
        Precio.setText("");
        Descripcion.setText("");
        Codigo.setEditable(true);

        piezas = new ArrayList<>(lista);
        String texto = "";

        for (Pieza pieza : piezas) {
            texto += pieza.getCodigo() + " | " + pieza.getNombre() + " | " + pieza.getPrecio() + " € | " + pieza.getDescripcion() + "\n";
        }

        Lista.setText(texto);
        Lista.setCaretPosition(0);
    }

    // CARGAR LA PIEZA SELECCIONADA
    // Copia al formulario los datos del registro seleccionado para editarlo.
    private void rellenarCampos(Pieza pieza) {
        Codigo.setText(pieza.getCodigo());
        Nombre.setText(pieza.getNombre());
        Precio.setText(pieza.getPrecio().toString());
        Descripcion.setText(pieza.getDescripcion() == null ? "" : pieza.getDescripcion());
        Codigo.setEditable(false);
    }

    // LIMPIAR
    // Deja el formulario preparado para introducir otro registro.
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
