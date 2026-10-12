package controlador;

import dao.LibroDAO;
import dao.PrestamoDAO;
import dao.EstudianteDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador que gestiona la lógica de préstamos y devoluciones.
 * Implementa concurrencia mediante hilos independientes para ejecutar
 * operaciones de préstamo/devolución sin congelar la interfaz gráfica.
 * Utiliza sincronización (synchronized en LibroDAO) para garantizar
 * la consistencia del stock ante accesos simultáneos.
 */
public class PrestamoController {

    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;
    private final EstudianteDAO estudianteDAO;

    public PrestamoController() {
        this.prestamoDAO = new PrestamoDAO();
        this.libroDAO = new LibroDAO();
        this.estudianteDAO = new EstudianteDAO();
    }

    /**
     * Registra un préstamo en un hilo independiente.
     * Proceso: 1) Descuenta stock (synchronized), 2) Crea préstamo, 3) Notifica UI.
     * Si falla, revierte el stock. La UI permanece responsive.
     */
    public void registrarPrestamoAsync(int idEstudiante, int idLibro,
                                        Runnable onSuccess, Runnable onError) {
        new Thread(() -> {
            try {
                // Paso 1: Descontar stock (método synchronized en LibroDAO)
                boolean stockDescontado = libroDAO.actualizarStock(idLibro, -1);
                if (!stockDescontado) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                                "No hay stock disponible para este libro.",
                                "Sin Stock", JOptionPane.WARNING_MESSAGE);
                        onError.run();
                    });
                    return;
                }

                // Paso 2: Crear el registro de préstamo
                Prestamo prestamo = new Prestamo();
                prestamo.setIdEstudiante(idEstudiante);
                prestamo.setIdLibro(idLibro);
                prestamo.setFechaPrestamo(LocalDate.now());
                prestamo.setFechaDevolucion(LocalDate.now().plusDays(7));
                prestamo.setDevuelto(false);

                int idGenerado = prestamoDAO.create(prestamo);

                if (idGenerado > 0) {
                    // Paso 3: Notificar éxito en el EDT
                    SwingUtilities.invokeLater(onSuccess);
                } else {
                    // Revertir stock si falló la inserción
                    libroDAO.actualizarStock(idLibro, +1);
                    SwingUtilities.invokeLater(onError);
                }
            } catch (SQLException ex) {
                // Intentar revertir stock en caso de excepción
                try { libroDAO.actualizarStock(idLibro, +1); } catch (SQLException ignored) {}
                SwingUtilities.invokeLater(onError);
            }
        }, "HiloPrestamo-" + System.currentTimeMillis()).start();
    }

    /**
     * Registra una devolución en un hilo independiente.
     * Marca el préstamo como devuelto y restaura el stock del libro correspondiente.
     */
    public void registrarDevolucionAsync(int idPrestamo,
                                          Runnable onSuccess, Runnable onError) {
        new Thread(() -> {
            try {
                Prestamo prestamo = prestamoDAO.findById(idPrestamo);
                if (prestamo != null && !prestamo.isDevuelto()) {
                    prestamo.setDevuelto(true);
                    boolean actualizado = prestamoDAO.update(prestamo);
                    if (actualizado) {
                        libroDAO.actualizarStock(prestamo.getIdLibro(), +1);
                        SwingUtilities.invokeLater(onSuccess);
                    } else {
                        SwingUtilities.invokeLater(onError);
                    }
                } else {
                    SwingUtilities.invokeLater(onError);
                }
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(onError);
            }
        }, "HiloDevolucion-" + System.currentTimeMillis()).start();
    }

    public List<Prestamo> listarTodos() throws SQLException { return prestamoDAO.readAll(); }
    public List<Estudiante> listarEstudiantes() throws SQLException { return estudianteDAO.readAll(); }
    public List<Libro> listarLibros() throws SQLException { return libroDAO.readAll(); }
}
