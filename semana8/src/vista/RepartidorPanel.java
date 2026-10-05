package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel Swing para la gestión CRUD completa de Repartidores.
 * Permite registrar, editar, eliminar y listar repartidores en un JTable.
 */
public class RepartidorPanel extends JPanel {

    private final RepartidorDAO repartidorDAO;
    private final Runnable onDatosModificadosCallback;

    // Componentes del formulario
    private JTextField txtId;
    private JTextField txtNombre;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Componentes de la tabla
    private JTable tablaRepartidores;
    private DefaultTableModel modeloTabla;

    public RepartidorPanel(RepartidorDAO dao, Runnable onDatosModificadosCallback) {
        this.repartidorDAO = dao;
        this.onDatosModificadosCallback = onDatosModificadosCallback;
        initUI();
        cargarDatosTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Izquierdo: Formulario de entrada
        JPanel panelFormulario = new JPanel(new BorderLayout(5, 5));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Datos del Repartidor"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelFormulario.setPreferredSize(new Dimension(320, 0));

        // Campos
        JPanel panelCampos = new JPanel(new GridLayout(4, 1, 5, 5));

        panelCampos.add(new JLabel("ID (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Nombre Completo (*):"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelFormulario.add(panelCampos, BorderLayout.NORTH);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 8));
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar Selección");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelFormulario.add(panelBotones, BorderLayout.SOUTH);
        add(panelFormulario, BorderLayout.WEST);

        // Panel Central: Tabla y herramientas
        JPanel panelTabla = new JPanel(new BorderLayout(5, 5));
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Listado de Repartidores Registrados"),
                new EmptyBorder(5, 5, 5, 5)
        ));

        String[] columnas = {"ID", "Nombre del Repartidor"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };

        tablaRepartidores = new JTable(modeloTabla);
        tablaRepartidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRepartidores.getTableHeader().setReorderingAllowed(false);
        tablaRepartidores.setRowHeight(22);

        JScrollPane scrollTabla = new JScrollPane(tablaRepartidores);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        // Botón de recarga manual
        JButton btnRecargar = new JButton("Recargar Tabla");
        JPanel panelInferiorTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferiorTabla.add(btnRecargar);
        panelTabla.add(panelInferiorTabla, BorderLayout.SOUTH);

        add(panelTabla, BorderLayout.CENTER);

        // Eventos
        configurarEventos(btnRecargar);
    }

    private void configurarEventos(JButton btnRecargar) {
        // Evento Registrar
        btnRegistrar.addActionListener(e -> registrarRepartidor());

        // Evento Actualizar
        btnActualizar.addActionListener(e -> actualizarRepartidor());

        // Evento Eliminar
        btnEliminar.addActionListener(e -> eliminarRepartidor());

        // Evento Limpiar
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Evento Recargar
        btnRecargar.addActionListener(e -> cargarDatosTabla());

        // Selección de fila en la tabla
        tablaRepartidores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaRepartidores.getSelectedRow() != -1) {
                int fila = tablaRepartidores.getSelectedRow();
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());

                btnRegistrar.setEnabled(false);
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });
    }

    /**
     * Valida entradas y registra un nuevo repartidor en la BD.
     */
    private void registrarRepartidor() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del repartidor es obligatorio.",
                    "Validación de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "El nombre debe contener al menos 3 caracteres.",
                    "Validación de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            Repartidor nuevo = new Repartidor(nombre);
            int idGenerado = repartidorDAO.create(nuevo);

            if (idGenerado > 0) {
                JOptionPane.showMessageDialog(this,
                        "Repartidor registrado exitosamente con ID: " + idGenerado,
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
                notificarCambio();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el repartidor en la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al registrar: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida entradas y actualiza un repartidor seleccionado.
     */
    private void actualizarRepartidor() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un repartidor de la tabla para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del repartidor no puede estar vacío.",
                    "Validación de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());
            Repartidor repartidor = new Repartidor(id, nombre);

            boolean exito = repartidorDAO.update(repartidor);
            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Repartidor actualizado correctamente.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
                notificarCambio();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el repartidor con ID " + id + " para actualizar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al actualizar: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Confirma y elimina el repartidor seleccionado.
     */
    private void eliminarRepartidor() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un repartidor de la tabla para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText();

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al repartidor #" + id + " (" + nombre + ")?\n" +
                "Nota: Esto también podría eliminar las entregas asociadas.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = repartidorDAO.delete(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                            "Repartidor eliminado exitosamente.",
                            "Operación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarDatosTabla();
                    notificarCambio();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el repartidor.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error de base de datos al eliminar: " + ex.getMessage(),
                        "Error SQL",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga todos los repartidores desde la base de datos a la tabla visual.
     */
    public void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Repartidor> repartidores = repartidorDAO.readAll();
            for (Repartidor r : repartidores) {
                modeloTabla.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la lista de repartidores: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resetea el formulario a su estado inicial.
     */
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        tablaRepartidores.clearSelection();
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void notificarCambio() {
        if (onDatosModificadosCallback != null) {
            onDatosModificadosCallback.run();
        }
    }
}
