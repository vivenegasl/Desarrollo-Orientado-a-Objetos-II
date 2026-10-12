package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión a la base de datos MySQL utilizando el patrón Singleton.
 * Garantiza una única instancia de conexión compartida para toda la aplicación,
 * con reconexión automática si la conexión se cierra o pierde.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "biblioteca_escolar";
    private static String user = "root";
    private static String password = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ADVERTENCIA: No se encontró el driver MySQL JDBC: " + e.getMessage());
        }
    }

    /**
     * Constructor privado — solo se invoca desde getInstance().
     */
    private DatabaseConnection() throws SQLException {
        this.connection = crearConexion();
    }

    /**
     * Retorna la instancia única de DatabaseConnection (Singleton).
     * Si la conexión fue cerrada o es nula, la restablece automáticamente.
     */
    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retorna la conexión compartida (Singleton).
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Crea y retorna una nueva conexión independiente.
     * Usar en operaciones CRUD con try-with-resources para que se cierre automáticamente.
     */
    public static Connection getNewConnection() throws SQLException {
        return crearConexion();
    }

    /**
     * Prueba si es posible conectarse a la base de datos.
     */
    public static boolean probarConexion() {
        try (Connection conn = getNewConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Permite personalizar las credenciales de conexión en tiempo de ejecución.
     */
    public static void configurar(String nuevoHost, String nuevoPuerto, String nuevaDb, String nuevoUser, String nuevoPass) {
        host = nuevoHost;
        port = nuevoPuerto;
        dbName = nuevaDb;
        user = nuevoUser;
        password = nuevoPass;
        instance = null; // Forzar reconexión con nuevos parámetros
    }

    private static Connection crearConexion() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        return DriverManager.getConnection(url, user, password);
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
}
