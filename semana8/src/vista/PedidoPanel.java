package vista;

import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel Swing para la gestión CRUD completa de Pedidos.
 * Permite registrar, editar, eliminar y listar pedidos en un JTable,
 * incluyendo filtros dinámicos por Tipo y por Estado.
 */
public class PedidoPanel extends JPanel {

    private final PedidoDAO pedidoDAO;
    private final Runnable onDatosModificadosCallback;

    // Componentes del formulario
    private JTextField txtId;
    private JTextField txtDireccion;
    private JComboBox<TipoPedido> cmbTipo;
    private JComboBox<EstadoPedido> cmbEstado;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    // Componentes de la tabla y filtros
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroTipo;
    private JComboBox<String> cmbFiltroEstado;

    public PedidoPanel(PedidoDAO dao, Runnable onDatosModificadosCallback) {
        this.pedidoDAO = dao;
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
                new TitledBorder("Datos del Pedido"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelFormulario.setPreferredSize(new Dimension(340, 0));

        // Campos del formulario
        JPanel panelCampos = new JPanel(new GridLayout(8, 1, 5, 5));

        panelCampos.add(new JLabel("ID (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Dirección de Entrega (*):"));
        txtDireccion = new JTextField();
        panelCampos.add(txtDireccion);

        panelCampos.add(new JLabel("Tipo de Pedido:"));
        cmbTipo = new JComboBox<>(TipoPedido.values());
        panelCampos.add(cmbTipo);

        panelCampos.add(new JLabel("Estado del Pedido:"));
        cmbEstado = new JComboBox<>(EstadoPedido.values());
        panelCampos.add(cmbEstado);

        panelFormulario.add(panelCampos, BorderLayout.NORTH);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 8));
        btnRegistrar = new JButton("Registrar Pedido");
        btnActualizar = new JButton("Actualizar Pedido");
        btnEliminar = new JButton("Eliminar Pedido");
        btnLimpiar = new JButton("Limpiar Selección");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelFormulario.add(panelBotones, BorderLayout.SOUTH);
        add(panelFormulario, BorderLayout.WEST);

        // Panel Central: Filtros + Tabla
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));

        // Barra superior de Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelFiltros.setBorder(new TitledBorder("Filtros de Búsqueda"));

        panelFiltros.add(new JLabel("Tipo:"));
        cmbFiltroTipo = new JComboBox<>(new String[]{"TODOS", "COMIDA", "ENCOMIENDA", "EXPRESS"});
        panelFiltros.add(cmbFiltroTipo);

        panelFiltros.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
        panelFiltros.add(cmbFiltroEstado);

        JButton btnFiltrar = new JButton("Aplicar Filtro");
        JButton btnRestablecerFiltros = new JButton("Restablecer");
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnRestablecerFiltros);

        panelCentral.add(panelFiltros, BorderLayout.NORTH);

        // Tabla de Pedidos
        String[] columnas = {"ID", "Dirección", "Tipo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
        tablaPedidos.setRowHeight(22);

        JScrollPane scrollTabla = new JScrollPane(tablaPedidos);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Configuración de eventos
        configurarEventos(btnFiltrar, btnRestablecerFiltros);
    }

    private void configurarEventos(JButton btnFiltrar, JButton btnRestablecerFiltros) {
        // Evento Registrar
        btnRegistrar.addActionListener(e -> registrarPedido());

        // Evento Actualizar
        btnActualizar.addActionListener(e -> actualizarPedido());

        // Evento Eliminar
        btnEliminar.addActionListener(e -> eliminarPedido());

        // Evento Limpiar
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Eventos de Filtro
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnRestablecerFiltros.addActionListener(e -> {
            cmbFiltroTipo.setSelectedIndex(0);
            cmbFiltroEstado.setSelectedIndex(0);
            cargarDatosTabla();
        });

        // Selección de fila en la tabla
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPedidos.getSelectedRow() != -1) {
                int fila = tablaPedidos.getSelectedRow();
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtDireccion.setText(modeloTabla.getValueAt(fila, 1).toString());

                String tipoStr = modeloTabla.getValueAt(fila, 2).toString();
                cmbTipo.setSelectedItem(TipoPedido.valueOf(tipoStr));

                String estadoStr = modeloTabla.getValueAt(fila, 3).toString();
                cmbEstado.setSelectedItem(EstadoPedido.valueOf(estadoStr));

                btnRegistrar.setEnabled(false);
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });
    }

    /**
     * Valida y registra un nuevo pedido.
     */
    private void registrarPedido() {
        String direccion = txtDireccion.getText().trim();
        TipoPedido tipo = (TipoPedido) cmbTipo.getSelectedItem();
        EstadoPedido estado = (EstadoPedido) cmbEstado.getSelectedItem();

        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La dirección de entrega es obligatoria.",
                    "Validación de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }

        try {
            Pedido nuevo = new Pedido(direccion, tipo, estado);
            int idGenerado = pedidoDAO.create(nuevo);

            if (idGenerado > 0) {
                JOptionPane.showMessageDialog(this,
                        "Pedido #" + idGenerado + " registrado con éxito.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
                notificarCambio();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el pedido en la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al registrar el pedido: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida y actualiza un pedido existente.
     */
    private void actualizarPedido() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un pedido de la tabla para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String direccion = txtDireccion.getText().trim();
        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La dirección no puede estar vacía.",
                    "Validación de Entrada",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());
            TipoPedido tipo = (TipoPedido) cmbTipo.getSelectedItem();
            EstadoPedido estado = (EstadoPedido) cmbEstado.getSelectedItem();

            Pedido pedido = new Pedido(id, direccion, tipo, estado);
            boolean exito = pedidoDAO.update(pedido);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Pedido #" + id + " actualizado correctamente.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
                notificarCambio();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el pedido con ID " + id + " para actualizar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al actualizar el pedido: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Confirma y elimina el pedido seleccionado.
     */
    private void eliminarPedido() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un pedido de la tabla para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el pedido #" + id + "?\n" +
                "Nota: También se eliminarán las entregas asociadas a este pedido.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = pedidoDAO.delete(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                            "Pedido eliminado exitosamente.",
                            "Operación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarDatosTabla();
                    notificarCambio();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el pedido.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error de base de datos al eliminar el pedido: " + ex.getMessage(),
                        "Error SQL",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga todos los pedidos sin filtros.
     */
    public void cargarDatosTabla() {
        try {
            List<Pedido> pedidos = pedidoDAO.readAll();
            llenarTabla(pedidos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pedidos: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aplica filtros opcionales de Tipo y Estado seleccionados en los combos de búsqueda.
     */
    private void aplicarFiltros() {
        String seleccionTipo = (String) cmbFiltroTipo.getSelectedItem();
        String seleccionEstado = (String) cmbFiltroEstado.getSelectedItem();

        TipoPedido tipoFiltro = "TODOS".equals(seleccionTipo) ? null : TipoPedido.valueOf(seleccionTipo);
        EstadoPedido estadoFiltro = "TODOS".equals(seleccionEstado) ? null : EstadoPedido.valueOf(seleccionEstado);

        try {
            List<Pedido> pedidos = pedidoDAO.readByFiltros(tipoFiltro, estadoFiltro);
            llenarTabla(pedidos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al filtrar pedidos: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarTabla(List<Pedido> pedidos) {
        modeloTabla.setRowCount(0);
        for (Pedido p : pedidos) {
            modeloTabla.addRow(new Object[]{
                    p.getId(),
                    p.getDireccion(),
                    p.getTipo().name(),
                    p.getEstado().name()
            });
        }
    }

    /**
     * Resetea el formulario a su estado inicial.
     */
    private void limpiarFormulario() {
        txtId.setText("");
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tablaPedidos.clearSelection();
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
