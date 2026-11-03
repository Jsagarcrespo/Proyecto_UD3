package org.example;

import org.example.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                VentanaPrincipal ventana = new VentanaPrincipal();

                ventana.setVisible(true);
            }
        });
    }
}
