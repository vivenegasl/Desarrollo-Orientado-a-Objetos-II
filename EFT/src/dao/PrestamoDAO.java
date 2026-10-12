package dao;

import modelo.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla prestamos. Implementa Reporteable.
 */
public class PrestamoDAO implements Reporteable {

    public int create(Prestamo p) throws SQLException {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdEstudiante());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFechaPrestamo() != null ? java.sql.Date.valueOf(p.getFechaPrestamo()) : null);
            ps.setDate(4, p.getFechaDevolucion() != null ? java.sql.Date.valueOf(p.getFechaDevolucion()) : null);
            ps.setBoolean(5, p.isDevuelto());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        p.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    public List<Prestamo> readAll() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " +
                     "e.nombre AS nombre_estudiante, l.titulo AS titulo_libro " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id " +
                     "JOIN libros l ON p.id_libro = l.id " +
                     "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setIdEstudiante(rs.getInt("id_estudiante"));
                p.setIdLibro(rs.getInt("id_libro"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo") != null ? rs.getDate("fecha_prestamo").toLocalDate() : null);
                p.setFechaDevolucion(rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion").toLocalDate() : null);
                p.setDevuelto(rs.getBoolean("devuelto"));
                p.setNombreEstudiante(rs.getString("nombre_estudiante"));
                p.setTituloLibro(rs.getString("titulo_libro"));
                prestamos.add(p);
            }
        }
        return prestamos;
    }

    public Prestamo findById(int id) throws SQLException {
        String sql = "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, " +
                     "e.nombre AS nombre_estudiante, l.titulo AS titulo_libro " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id " +
                     "JOIN libros l ON p.id_libro = l.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setIdEstudiante(rs.getInt("id_estudiante"));
                    p.setIdLibro(rs.getInt("id_libro"));
                    p.setFechaPrestamo(rs.getDate("fecha_prestamo") != null ? rs.getDate("fecha_prestamo").toLocalDate() : null);
                    p.setFechaDevolucion(rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion").toLocalDate() : null);
                    p.setDevuelto(rs.getBoolean("devuelto"));
                    p.setNombreEstudiante(rs.getString("nombre_estudiante"));
                    p.setTituloLibro(rs.getString("titulo_libro"));
                    return p;
                }
            }
        }
        return null;
    }

    public boolean update(Prestamo p) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = ?, fecha_devolucion = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, p.isDevuelto());
            ps.setDate(2, p.getFechaDevolucion() != null ? java.sql.Date.valueOf(p.getFechaDevolucion()) : null);
            ps.setInt(3, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<String[]> generarReporteLibrosMasPrestados() throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        String sql = "SELECT l.titulo, COUNT(p.id) AS total FROM prestamos p " +
                     "JOIN libros l ON p.id_libro = l.id GROUP BY l.id, l.titulo ORDER BY total DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reporte.add(new String[]{rs.getString("titulo"), String.valueOf(rs.getInt("total"))});
            }
        }
        return reporte;
    }

    @Override
    public List<String[]> generarReporteHistorialEstudiante(int idEstudiante) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        String sql = "SELECT l.titulo, p.fecha_prestamo, p.fecha_devolucion, " +
                     "CASE WHEN p.devuelto THEN 'Devuelto' ELSE 'Pendiente' END AS estado " +
                     "FROM prestamos p JOIN libros l ON p.id_libro = l.id WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fDev = rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion").toString() : "Pendiente";
                    reporte.add(new String[]{
                        rs.getString("titulo"),
                        rs.getDate("fecha_prestamo").toString(),
                        fDev,
                        rs.getString("estado")
                    });
                }
            }
        }
        return reporte;
    }

    @Override
    public List<String[]> generarReporteLibrosEnPrestamo() throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        String sql = "SELECT l.titulo, e.nombre, p.fecha_prestamo, p.fecha_devolucion " +
                     "FROM prestamos p JOIN libros l ON p.id_libro = l.id " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id WHERE p.devuelto = FALSE ORDER BY p.fecha_prestamo ASC";
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fDev = rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion").toString() : "Sin definir";
                reporte.add(new String[]{
                    rs.getString("titulo"),
                    rs.getString("nombre"),
                    rs.getDate("fecha_prestamo").toString(),
                    fDev
                });
            }
        }
        return reporte;
    }
}
