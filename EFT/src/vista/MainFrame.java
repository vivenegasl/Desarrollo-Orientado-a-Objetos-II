package vista;

import dao.DatabaseConnection;
import modelo.RolUsuario;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;

/**
 * Ventana principal del sistema.
 */
public class MainFrame extends JFrame {

    private final Usuario usuarioLogueado;

    public MainFrame(Usuario usuario) {
        this.usuarioLogueado = usuario;
        
        setTitle("Biblioteca Escolar - Panel de Administración");
        setSize(1050, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(28, 40, 51));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Biblioteca Escolar");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JLabel lblUser = new JLabel("Sesión: " + usuario.getInfoResumida());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // MenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        
        JMenuItem itemProbarConexion = new JMenuItem("Probar Conexión");
        JMenuItem itemConfigurar = new JMenuItem("Configurar Conexión");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        JMenuItem itemSalir = new JMenuItem("Salir");
        
        itemProbarConexion.addActionListener(e -> probarConexion());
        itemConfigurar.addActionListener(e -> configurarConexion());
        itemCerrarSesion.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        itemSalir.addActionListener(e -> System.exit(0));
        
        menuArchivo.add(itemProbarConexion);
        menuArchivo.add(itemConfigurar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrarSesion);
        menuArchivo.add(itemSalir);
        
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> JOptionPane.showMessageDialog(this, "Biblioteca Escolar v1.0\nDesarrollado para EFT", "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if (usuario.getRol() == RolUsuario.BIBLIOTECARIO) {
            tabbedPane.addTab("Libros", new LibroPanel());
            tabbedPane.addTab("Estudiantes", new EstudiantePanel());
            tabbedPane.addTab("Categorías", new CategoriaPanel());
        }
        
        tabbedPane.addTab("Préstamos", new PrestamoPanel());
        tabbedPane.addTab("Reportes", new ReportePanel());
        
        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel lblStatus = new JLabel("Duoc UC - DOO II - EFT | Estado BD: Conectado");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(lblStatus, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void probarConexion() {
        if (DatabaseConnection.probarConexion()) {
            JOptionPane.showMessageDialog(this, "Conexión a la base de datos exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Fallo al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarConexion() {
        JTextField hostField = new JTextField(DatabaseConnection.getHost());
        JTextField portField = new JTextField(DatabaseConnection.getPort());
        JTextField dbNameField = new JTextField(DatabaseConnection.getDbName());
        JTextField userField = new JTextField(DatabaseConnection.getUser());
        JPasswordField passField = new JPasswordField(DatabaseConnection.getPassword());

        Object[] message = {
            "Host:", hostField,
            "Puerto:", portField,
            "Base de Datos:", dbNameField,
            "Usuario:", userField,
            "Contraseña:", passField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Configurar Conexión BD", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            DatabaseConnection.configurar(
                hostField.getText(),
                portField.getText(),
                dbNameField.getText(),
                userField.getText(),
                new String(passField.getPassword())
            );
            JOptionPane.showMessageDialog(this, "Configuración actualizada. Intente probar la conexión.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
