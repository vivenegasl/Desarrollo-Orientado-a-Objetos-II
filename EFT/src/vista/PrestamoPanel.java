package vista;

import controlador.PrestamoController;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoPanel extends JPanel {
    private final PrestamoController controller;
    private JComboBox<Estudiante> cmbEstudiante;
    private JComboBox<Libro> cmbLibro;
    private JButton btnPrestamo;
    private JLabel lblEstadoPrestamo;
    
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnDevolucion, btnActualizar;

    public PrestamoPanel() {
        controller = new PrestamoController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Section: Nuevo Préstamo
        JPanel panelNuevo = new JPanel(new GridLayout(3, 2, 10, 10));
        panelNuevo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Nuevo Préstamo", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));
        
        panelNuevo.add(new JLabel("Estudiante:"));
        cmbEstudiante = new JComboBox<>();
        panelNuevo.add(cmbEstudiante);

        panelNuevo.add(new JLabel("Libro:"));
        cmbLibro = new JComboBox<>();
        panelNuevo.add(cmbLibro);

        btnPrestamo = new JButton("Realizar Préstamo");
        panelNuevo.add(btnPrestamo);

        lblEstadoPrestamo = new JLabel("Procesando...");
        lblEstadoPrestamo.setForeground(new Color(0, 102, 204));
        lblEstadoPrestamo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoPrestamo.setVisible(false);
        panelNuevo.add(lblEstadoPrestamo);

        add(panelNuevo, BorderLayout.NORTH);

        // Bottom Section: Préstamos Activos
        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Préstamos Activos", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        String[] columnas = {"ID", "Estudiante", "Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom Renderer for "ATRASADO"
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = table.getValueAt(row, 5).toString();
                if ("ATRASADO".equals(estado)) {
                    c.setForeground(Color.RED);
                    if (!isSelected) setBackground(new Color(255, 230, 230));
                } else {
                    c.setForeground(Color.BLACK);
                    if (!isSelected) setBackground(Color.WHITE);
                }
                return c;
            }
        });
        
        panelLista.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotonesBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDevolucion = new JButton("Registrar Devolución");
        btnDevolucion.setEnabled(false);
        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesBottom.add(btnDevolucion);
        panelBotonesBottom.add(btnActualizar);
        panelLista.add(panelBotonesBottom, BorderLayout.SOUTH);

        add(panelLista, BorderLayout.CENTER);

        // Events
        btnPrestamo.addActionListener(e -> registrarPrestamo());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        btnActualizar.addActionListener(e -> cargarDatos());
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                String estado = tabla.getValueAt(tabla.getSelectedRow(), 5).toString();
                btnDevolucion.setEnabled(!"Devuelto".equals(estado));
            } else {
                btnDevolucion.setEnabled(false);
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            // Cargar combos
            cmbEstudiante.removeAllItems();
            for (Estudiante est : controller.listarEstudiantes()) {
                cmbEstudiante.addItem(est);
            }

            cmbLibro.removeAllItems();
            for (Libro lib : controller.listarLibros()) {
                if (lib.getStock() > 0) cmbLibro.addItem(lib);
            }

            // Cargar tabla
            modeloTabla.setRowCount(0);
            for (Prestamo p : controller.listarTodos()) {
                String estado = "Vigente";
                if (p.isDevuelto()) {
                    estado = "Devuelto";
                } else if (p.getFechaDevolucion() != null && p.getFechaDevolucion().isBefore(LocalDate.now())) {
                    estado = "ATRASADO";
                }
                
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), estado
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPrestamo() {
        Estudiante est = (Estudiante) cmbEstudiante.getSelectedItem();
        Libro lib = (Libro) cmbLibro.getSelectedItem();

        if (est == null || lib == null) {
            JOptionPane.showMessageDialog(this, "Seleccione estudiante y libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnPrestamo.setEnabled(false);
        lblEstadoPrestamo.setVisible(true);

        controller.registrarPrestamoAsync(est.getId(), lib.getId(), 
            () -> { // onSuccess
                lblEstadoPrestamo.setVisible(false);
                btnPrestamo.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Préstamo registrado exitosamente.");
                cargarDatos();
            },
            () -> { // onError
                lblEstadoPrestamo.setVisible(false);
                btnPrestamo.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Error al registrar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void registrarDevolucion() {
        int row = tabla.getSelectedRow();
        if (row == -1) return;

        int idPrestamo = (Integer) tabla.getValueAt(row, 0);

        btnDevolucion.setEnabled(false);
        controller.registrarDevolucionAsync(idPrestamo,
            () -> {
                JOptionPane.showMessageDialog(this, "Devolución registrada exitosamente.");
                cargarDatos();
            },
            () -> {
                JOptionPane.showMessageDialog(this, "Error al registrar devolución.", "Error", JOptionPane.ERROR_MESSAGE);
                btnDevolucion.setEnabled(true);
            }
        );
    }
}
