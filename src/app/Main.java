package app;

import controlador.PromedioControlador;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PromedioControlador controlador = new PromedioControlador();
            new VentanaPrincipal(controlador).setVisible(true);
        });
    }
}
