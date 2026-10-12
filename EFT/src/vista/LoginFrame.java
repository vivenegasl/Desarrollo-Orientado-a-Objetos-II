package vista;

import controlador.LoginController;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Vista para el inicio de sesión.
 */
public class LoginFrame extends JFrame {
    private final JTextField txtRut;
    private final JPasswordField txtContrasena;
    private final LoginController controller;

    public LoginFrame() {
        controller = new LoginController();
        setTitle("Biblioteca Escolar - Inicio de Sesión");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de encabezado
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(28, 40, 51));
        headerPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JLabel lblTitle = new JLabel("Sistema de Gestión de Biblioteca Escolar", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblSub = new JLabel("Ingrese sus credenciales para continuar", SwingConstants.CENTER);
        lblSub.setForeground(Color.WHITE);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JLabel lblRut = new JLabel("RUT:");
        lblRut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtRut = new JTextField();
        
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContrasena = new JPasswordField();
        
        centerPanel.add(lblRut);
        centerPanel.add(txtRut);
        centerPanel.add(lblPass);
        centerPanel.add(txtContrasena);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");
        
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        bottomPanel.add(btnIngresar);
        bottomPanel.add(btnSalir);
        add(bottomPanel, BorderLayout.SOUTH);

        // Eventos
        btnSalir.addActionListener(e -> System.exit(0));
        
        btnIngresar.addActionListener(e -> autenticar());
        
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticar();
                }
            }
        });
    }

    private void autenticar() {
        String rut = txtRut.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        if (rut.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = controller.autenticar(rut, contrasena);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bienvenido: " + usuario.getInfoResumida(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                new MainFrame(usuario).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
