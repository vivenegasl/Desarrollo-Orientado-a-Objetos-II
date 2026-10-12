package dao;

import modelo.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla categorias.
 */
public class CategoriaDAO {

    public int create(Categoria c) throws SQLException {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        c.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    public List<Categoria> readAll() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre ASC";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                categorias.add(c);
            }
        }
        return categorias;
    }

    public Categoria findById(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setId(rs.getInt("id"));
                    c.setNombre(rs.getString("nombre"));
                    return c;
                }
            }
        }
        return null;
    }

    public boolean update(Categoria c) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
