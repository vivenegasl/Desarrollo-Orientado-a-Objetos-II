package controlador;

import dao.PrestamoDAO;
import dao.Reporteable;
import dao.EstudianteDAO;
import modelo.Estudiante;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la generación de reportes del sistema.
 * Utiliza la interfaz Reporteable (polimorfismo) para invocar los métodos de reporte.
 */
public class ReporteController {

    private final Reporteable reporteService;
    private final EstudianteDAO estudianteDAO;

    public ReporteController() {
        this.reporteService = new PrestamoDAO(); // Polimorfismo: se usa la interfaz
        this.estudianteDAO = new EstudianteDAO();
    }

    public List<String[]> obtenerLibrosMasPrestados() throws SQLException {
        return reporteService.generarReporteLibrosMasPrestados();
    }

    public List<String[]> obtenerHistorialEstudiante(int idEstudiante) throws SQLException {
        return reporteService.generarReporteHistorialEstudiante(idEstudiante);
    }

    public List<String[]> obtenerLibrosEnPrestamo() throws SQLException {
        return reporteService.generarReporteLibrosEnPrestamo();
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }
}
