package dao;

import modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla libros.
 */
public class LibroDAO {

    public int create(Libro l) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getIsbn());
            ps.setString(4, l.getEditorial());
            ps.setInt(5, l.getStock());
            ps.setInt(6, l.getIdCategoria());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        l.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    public List<Libro> readAll() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, c.nombre AS nombre_cat " +
                     "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id ORDER BY l.id ASC";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Libro l = new Libro();
                l.setId(rs.getInt("id"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setIsbn(rs.getString("isbn"));
                l.setEditorial(rs.getString("editorial"));
                l.setStock(rs.getInt("stock"));
                l.setIdCategoria(rs.getInt("id_categoria"));
                l.setNombreCategoria(rs.getString("nombre_cat"));
                libros.add(l);
            }
        }
        return libros;
    }

    public Libro findById(int id) throws SQLException {
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, c.nombre AS nombre_cat " +
                     "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id WHERE l.id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro l = new Libro();
                    l.setId(rs.getInt("id"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setIsbn(rs.getString("isbn"));
                    l.setEditorial(rs.getString("editorial"));
                    l.setStock(rs.getInt("stock"));
                    l.setIdCategoria(rs.getInt("id_categoria"));
                    l.setNombreCategoria(rs.getString("nombre_cat"));
                    return l;
                }
            }
        }
        return null;
    }

    public boolean update(Libro l) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getIsbn());
            ps.setString(4, l.getEditorial());
            ps.setInt(5, l.getStock());
            ps.setInt(6, l.getIdCategoria());
            ps.setInt(7, l.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public synchronized boolean actualizarStock(int idLibro, int cantidad) throws SQLException {
        String sql = "UPDATE libros SET stock = stock + ? WHERE id = ? AND stock + ? >= 0";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idLibro);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        }
    }
}
