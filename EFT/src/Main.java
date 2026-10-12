import vista.LoginFrame;

import javax.swing.*;

/**
 * Punto de entrada principal para la aplicación Biblioteca Escolar.
 * Inicializa el Look and Feel del sistema e instancia la ventana de login.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
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
