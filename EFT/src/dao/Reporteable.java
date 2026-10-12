package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define la capacidad de generar reportes estadísticos.
 * Implementada por clases DAO que producen informes del sistema.
 * Demuestra el uso de interfaces y polimorfismo en el diseño.
 */
public interface Reporteable {

    /**
     * Genera un reporte de los libros más prestados.
     * @return Lista de arreglos [titulo, total_prestamos].
     */
    List<String[]> generarReporteLibrosMasPrestados() throws SQLException;

    /**
     * Genera el historial de préstamos de un estudiante específico.
     * @param idEstudiante ID del estudiante.
     * @return Lista de arreglos [titulo_libro, fecha_prestamo, fecha_devolucion, estado].
     */
    List<String[]> generarReporteHistorialEstudiante(int idEstudiante) throws SQLException;

    /**
     * Genera un listado de libros actualmente en préstamo (no devueltos).
     * @return Lista de arreglos [titulo, estudiante, fecha_prestamo, fecha_devolucion].
     */
    List<String[]> generarReporteLibrosEnPrestamo() throws SQLException;
}
