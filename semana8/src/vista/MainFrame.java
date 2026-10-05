package vista;

import dao.ConexionDB;
import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Ventana principal de la aplicación SpeedFast.
 * Integra los módulos de Repartidores, Pedidos y Entregas mediante un JTabbedPane,
 * manteniendo sincronizados los datos entre paneles y proveyendo utilidades de conexión.
 */
public class MainFrame extends JFrame {

    private final RepartidorDAO repartidorDAO;
    private final PedidoDAO pedidoDAO;
    private final EntregaDAO entregaDAO;

    private RepartidorPanel panelRepartidores;
    private PedidoPanel panelPedidos;
    private EntregaPanel panelEntregas;
    private JLabel lblEstadoConexion;

    public MainFrame() {
        // Inicialización de la capa DAO
        this.repartidorDAO = new RepartidorDAO();
        this.pedidoDAO = new PedidoDAO();
        this.entregaDAO = new EntregaDAO();

        initUI();
    }

    private void initUI() {
        setTitle("SpeedFast - Sistema de Gestión de Pedidos, Repartidores y Entregas (Semana 8)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        // Barra de Menús
        setJMenuBar(crearBarraMenu());

        // Contenedor principal
        JPanel contenedor = new JPanel(new BorderLayout());

        // Encabezado decorativo
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(new Color(28, 40, 51));
        panelEncabezado.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitulo = new JLabel("SpeedFast - Panel de Administración");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Módulo de Persistencia JDBC y Gestión de Operaciones CRUD");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(189, 195, 199));

        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);
        contenedor.add(panelEncabezado, BorderLayout.NORTH);

        // Pestañas (JTabbedPane)
        JTabbedPane tabbedPane = new JTabbedPane();

        // Callback para cuando se modifica un repartidor o pedido -> refresca combos en entregas
        Runnable callbackSincronizacion = () -> {
            if (panelEntregas != null) {
                panelEntregas.cargarCombos();
            }
        };

        panelRepartidores = new RepartidorPanel(repartidorDAO, callbackSincronizacion);
        panelPedidos = new PedidoPanel(pedidoDAO, callbackSincronizacion);
        panelEntregas = new EntregaPanel(entregaDAO, pedidoDAO, repartidorDAO);

        tabbedPane.addTab("  Repartidores  ", panelRepartidores);
        tabbedPane.addTab("  Pedidos  ", panelPedidos);
        tabbedPane.addTab("  Entregas  ", panelEntregas);

        // Listener para sincronizar automáticamente al cambiar de pestaña
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 2) { // Pestaña Entregas
                panelEntregas.cargarCombos();
                panelEntregas.cargarDatosTabla();
            } else if (selectedIndex == 1) { // Pestaña Pedidos
                panelPedidos.cargarDatosTabla();
            } else if (selectedIndex == 0) { // Pestaña Repartidores
                panelRepartidores.cargarDatosTabla();
            }
        });

        contenedor.add(tabbedPane, BorderLayout.CENTER);

        // Barra de estado inferior
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 12, 5, 12)
        ));
        panelEstado.setBackground(new Color(245, 245, 245));

        lblEstadoConexion = new JLabel();
        lblEstadoConexion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actualizarTextoEstadoConexion();
        panelEstado.add(lblEstadoConexion, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("Duoc UC - DOO II");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        panelEstado.add(lblInfo, BorderLayout.EAST);

        contenedor.add(panelEstado, BorderLayout.SOUTH);

        add(contenedor);
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemProbarConexion = new JMenuItem("Probar Conexión a Base de Datos");
        itemProbarConexion.addActionListener(e -> probarConexionBD());
        menuArchivo.add(itemProbarConexion);

        JMenuItem itemConfigurarDB = new JMenuItem("Configurar Parámetros de Conexión...");
        itemConfigurarDB.addActionListener(e -> abrirDialogoConfiguracionDB());
        menuArchivo.add(itemConfigurarDB);

        menuArchivo.addSeparator();

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de SpeedFast...");
        itemAcercaDe.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "SpeedFast - Sistema de Gestión de Envíos y Logística\n" +
                    "Evaluación Sumativa 3 - Semana 8\n" +
                    "Desarrollo Orientado a Objetos II\n\n" +
                    "• Arquitectura en 3 capas (Modelo, DAO, Vista)\n" +
                    "• Persistencia segura con MySQL y JDBC (PreparedStatement/ResultSet)\n" +
                    "• Interfaz gráfica construida con Java Swing",
                    "Acerca de",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        return menuBar;
    }

    private void probarConexionBD() {
        try (Connection conn = ConexionDB.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                JOptionPane.showMessageDialog(this,
                        "¡Conexión establecida exitosamente con la base de datos MySQL!\n\n" +
                        "Base de datos: " + ConexionDB.getDbName() + "\n" +
                        "Host: " + ConexionDB.getHost() + ":" + ConexionDB.getPort() + "\n" +
                        "Usuario: " + ConexionDB.getUser(),
                        "Conexión Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                actualizarTextoEstadoConexion();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar a la base de datos MySQL:\n" + ex.getMessage() +
                    "\n\nPuede ajustar el host, puerto, usuario o contraseña en 'Archivo -> Configurar Parámetros de Conexión'.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoConfiguracionDB() {
        JTextField txtHost = new JTextField(ConexionDB.getHost());
        JTextField txtPuerto = new JTextField(ConexionDB.getPort());
        JTextField txtDb = new JTextField(ConexionDB.getDbName());
        JTextField txtUser = new JTextField(ConexionDB.getUser());
        JPasswordField txtPass = new JPasswordField(ConexionDB.getPassword());

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Host:"));
        panel.add(txtHost);
        panel.add(new JLabel("Puerto:"));
        panel.add(txtPuerto);
        panel.add(new JLabel("Base de Datos:"));
        panel.add(txtDb);
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUser);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPass);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Configurar Parámetros de Conexión MySQL",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            ConexionDB.configurar(
                    txtHost.getText().trim(),
                    txtPuerto.getText().trim(),
                    txtDb.getText().trim(),
                    txtUser.getText().trim(),
                    new String(txtPass.getPassword())
            );
            actualizarTextoEstadoConexion();
            probarConexionBD();
            // Recargar datos en los paneles
            panelRepartidores.cargarDatosTabla();
            panelPedidos.cargarDatosTabla();
            panelEntregas.cargarCombos();
            panelEntregas.cargarDatosTabla();
        }
    }

    private String obtenerTextoEstadoConexion() {
        boolean conectada = ConexionDB.probarConexion();
        String estado = conectada ? "● Conectado a MySQL" : "○ Sin conexión a MySQL";
        return estado + " | BD: " + ConexionDB.getDbName() + " | Host: " + ConexionDB.getHost() + ":" +
                ConexionDB.getPort() + " | Usuario: " + ConexionDB.getUser();
    }

    private void actualizarTextoEstadoConexion() {
        if (lblEstadoConexion != null) {
            boolean conectada = ConexionDB.probarConexion();
            lblEstadoConexion.setText(obtenerTextoEstadoConexion());
            if (conectada) {
                lblEstadoConexion.setForeground(new Color(30, 130, 60)); // Verde
            } else {
                lblEstadoConexion.setForeground(new Color(200, 50, 40)); // Rojo
            }
        }
    }
}
