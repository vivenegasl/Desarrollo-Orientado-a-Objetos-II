package dao;

import modelo.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Repartidor.
 * Implementa operaciones CRUD completas utilizando PreparedStatement y ResultSet.
 */
public class RepartidorDAO {

    /**
     * Registra un nuevo repartidor en la base de datos.
     *
     * @param repartidor Objeto repartidor con el nombre a insertar.
     * @return El ID generado para el nuevo repartidor, o -1 si falla.
     * @throws SQLException Si ocurre un error durante la ejecución SQL.
     */
    public int create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, repartidor.getNombre());
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        repartidor.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Obtiene la lista completa de repartidores registrados.
     *
     * @return Lista de repartidores ordenados por ID.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     */
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id ASC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Repartidor r = new Repartidor();
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                lista.add(r);
            }
        }
        return lista;
    }

    /**
     * Busca un repartidor por su identificador único.
     *
     * @param id Identificador del repartidor.
     * @return Repartidor encontrado o null si no existe.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public Repartidor findById(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM repartidores WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Repartidor r = new Repartidor();
                    r.setId(rs.getInt("id"));
                    r.setNombre(rs.getString("nombre"));
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de un repartidor existente.
     *
     * @param repartidor Objeto con los datos actualizados y su ID.
     * @return true si se actualizó al menos una fila, false de lo contrario.
     * @throws SQLException Si ocurre un error en la actualización.
     */
    public boolean update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un repartidor por su ID.
     *
     * @param id Identificador del repartidor a eliminar.
     * @return true si se eliminó correctamente, false de lo contrario.
     * @throws SQLException Si ocurre un error al eliminar.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
