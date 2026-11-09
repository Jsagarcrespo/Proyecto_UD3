package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {

    public JPanel panelPrincipal;

    private JButton proveedoresButton;
    private JButton proyectosButton;
    private JButton piezasButton;
    private JButton sumnistrosButton;
    private JButton ayudaButton;
    private JButton salirButton;

    public VentanaPrincipal() {

        setTitle("Proyecto UD3 - Gestion de suministros");
        setContentPane(panelPrincipal);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // PROVEEDORES
        proveedoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaProvedores ventana = new VentanaProvedores();
                ventana.setVisible(true);

            }
        });

        // PROYECTOS
        proyectosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VentanaProyectos ventana = new VentanaProyectos();
                ventana.setVisible(true);
            }
        });

        // PIEZAS
        piezasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                VentanaPiezas ventana = new VentanaPiezas();
                ventana.setVisible(true);
            }
        });

        // SUMINISTROS
        sumnistrosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaGestiones ventana = new VentanaGestiones();
                ventana.setVisible(true);
            }
        });

        // AYUDA
        ayudaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Proyecto UD3 - Uso de ORMs\n" + "Gestion de proveedores, piezas, " + "proyectos y suministros.\n" + "Java, Hibernate y MariaDB.");
            }
        });

        // SALIR
        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int respuesta = JOptionPane.showConfirmDialog(VentanaPrincipal.this, "¿Quieres salir de la aplicacion?", "Salir", JOptionPane.YES_NO_OPTION);

                if (respuesta == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}
