package org.example;

/*
 * PUNTO DE ENTRADA DEL PROGRAMA
 * main() abre la ventana principal de Swing.
 * SwingUtilities.invokeLater() prepara la ventana en el hilo de Swing.
 */

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
