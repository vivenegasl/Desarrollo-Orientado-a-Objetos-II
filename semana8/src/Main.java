import vista.MainFrame;

import javax.swing.*;

/**
 * Punto de entrada principal para la aplicación SpeedFast.
 * Inicializa el Look and Feel del sistema e instancia la ventana principal en el hilo de eventos de Swing (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        // Ejecutar la interfaz en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error crítico al iniciar la aplicación: " + e.getMessage(),
                        "Error Fatal",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
