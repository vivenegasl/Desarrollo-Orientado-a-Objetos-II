package vista;

import controlador.ReporteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ReportePanel extends JPanel {
    private final ReporteController controller;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<Estudiante> cmbEstudiantes;

    public ReportePanel() {
        controller = new ReporteController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Controls
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelControles.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Opciones de Reporte", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        JButton btnMasPrestados = new JButton("Libros Más Prestados");
        JButton btnEnPrestamo = new JButton("Libros Actualmente en Préstamo");
        
        JPanel pnlEstudiante = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        cmbEstudiantes = new JComboBox<>();
        cargarEstudiantes();
        JButton btnHistorial = new JButton("Historial por Estudiante");
        pnlEstudiante.add(cmbEstudiantes);
        pnlEstudiante.add(btnHistorial);

        panelControles.add(btnMasPrestados);
        panelControles.add(pnlEstudiante);
        panelControles.add(btnEnPrestamo);

        add(panelControles, BorderLayout.NORTH);

        // Result Table
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Resultados del Reporte", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));
        
        modeloTabla = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panelResultados.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelResultados, BorderLayout.CENTER);

        // Events
        btnMasPrestados.addActionListener(e -> generarReporteMasPrestados());
        btnHistorial.addActionListener(e -> generarReporteHistorial());
        btnEnPrestamo.addActionListener(e -> generarReporteEnPrestamo());
    }

    private void cargarEstudiantes() {
        try {
            List<Estudiante> estudiantes = controller.listarEstudiantes();
            cmbEstudiantes.removeAllItems();
            for (Estudiante est : estudiantes) {
                cmbEstudiantes.addItem(est);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void generarReporteMasPrestados() {
        try {
            List<String[]> datos = controller.obtenerLibrosMasPrestados();
            String[] columnas = {"Título", "Total Préstamos"};
            actualizarTabla(datos, columnas);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void generarReporteHistorial() {
        Estudiante est = (Estudiante) cmbEstudiantes.getSelectedItem();
        if (est != null) {
            try {
                List<String[]> datos = controller.obtenerHistorialEstudiante(est.getId());
                String[] columnas = {"Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"};
                actualizarTabla(datos, columnas);
            } catch (SQLException ex) {
                mostrarError(ex);
            }
        }
    }

    private void generarReporteEnPrestamo() {
        try {
            List<String[]> datos = controller.obtenerLibrosEnPrestamo();
            String[] columnas = {"Libro", "Estudiante", "Fecha Préstamo", "Fecha Devolución"};
            actualizarTabla(datos, columnas);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }

    private void actualizarTabla(List<String[]> datos, String[] columnas) {
        modeloTabla.setDataVector(new Object[0][0], columnas);
        for (String[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    private void mostrarError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
