package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase responsable de gestionar la conexión con la base de datos MySQL (speedfast_db).
 * Centraliza las credenciales y parámetros de configuración para toda la aplicación.
 */
public class ConexionDB {

    // Parámetros de conexión predeterminados para MySQL local
    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "speedfast_db";
    private static String user = "root";
    private static String password = ""; // Modificar según contraseña local de MySQL si aplica

    static {
        try {
            // Carga explícita del driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ADVERTENCIA: No se encontró el driver MySQL JDBC (com.mysql.cj.jdbc.Driver): " + e.getMessage());
        }
    }

    /**
     * Obtiene una nueva conexión a la base de datos speedfast_db.
     *
     * @return Connection objeto de conexión activo.
     * @throws SQLException Si ocurre algún fallo de conexión.
     */
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Prueba si es posible conectarse a la base de datos con los parámetros actuales.
     *
     * @return true si la conexión fue exitosa, false en caso contrario.
     */
    public static boolean probarConexion() {
        try (Connection conn = getConnection()) {
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
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
}
