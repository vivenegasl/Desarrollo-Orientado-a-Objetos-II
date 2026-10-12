package dao;

import modelo.Bibliotecario;
import modelo.Estudiante;
import modelo.Usuario;

import java.sql.*;

/**
 * DAO para autenticación de usuarios del sistema.
 * Implementa la validación de credenciales y retorna el usuario
 * como Bibliotecario o Estudiante según su rol (polimorfismo).
 */
public class UsuarioDAO {

    /**
     * Autentica un usuario por RUT y contraseña.
     * Retorna un objeto Bibliotecario o Estudiante según el rol almacenado.
     * Demuestra polimorfismo: el tipo retornado es Usuario (abstracto).
     *
     * @param rut        RUT del usuario.
     * @param contrasena Contraseña del usuario.
     * @return Usuario autenticado (Bibliotecario o Estudiante), o null si no existe.
     */
    public Usuario autenticar(String rut, String contrasena) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contrasena, rol FROM usuarios WHERE rut = ? AND contrasena = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String rutDb = rs.getString("rut");
                    String correo = rs.getString("correo");
                    String pass = rs.getString("contrasena");
                    String rol = rs.getString("rol");

                    // Polimorfismo: se retorna Bibliotecario o Estudiante según el rol
                    if ("BIBLIOTECARIO".equals(rol)) {
                        return new Bibliotecario(id, nombre, rutDb, correo, pass);
                    } else {
                        // Para rol ESTUDIANTE, buscar datos adicionales en tabla estudiantes
                        Estudiante est = buscarEstudiantePorRut(rutDb);
                        if (est != null) {
                            return est;
                        }
                        // Si no está en tabla estudiantes, crear con datos básicos
                        return new Estudiante(id, nombre, rutDb, "", correo);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Busca un estudiante en la tabla 'estudiantes' por su RUT.
     */
    private Estudiante buscarEstudiantePorRut(String rut) throws SQLException {
        String sql = "SELECT id, nombre, rut, curso, correo FROM estudiantes WHERE rut = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Estudiante(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rut"),
                            rs.getString("curso"),
                            rs.getString("correo")
                    );
                }
            }
        }
        return null;
    }
}
