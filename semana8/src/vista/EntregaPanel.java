package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel Swing para la gestión CRUD completa de Entregas.
 * Permite asociar Pedidos y Repartidores seleccionándolos desde JComboBox cargados desde la base de datos,
 * especificando fecha y hora con validación completa y filtros de visualización.
 */
public class EntregaPanel extends JPanel {

    private final EntregaDAO entregaDAO;
    private final PedidoDAO pedidoDAO;
    private final RepartidorDAO repartidorDAO;

    // Formateadores estándar para SQL DATE y TIME
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMATO_HORA_CORTA = DateTimeFormatter.ofPattern("HH:mm");

    // Componentes del formulario
    private JTextField txtId;
    private JComboBox<Pedido> cmbPedido;
    private JComboBox<Repartidor> cmbRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnAhora;

    // Componentes de la tabla y filtros
    private JTable tablaEntregas;
    private DefaultTableModel modeloTabla;
    private JComboBox<Object> cmbFiltroPedido;
    private JComboBox<Object> cmbFiltroRepartidor;

    public EntregaPanel(EntregaDAO entregaDAO, PedidoDAO pedidoDAO, RepartidorDAO repartidorDAO) {
        this.entregaDAO = entregaDAO;
        this.pedidoDAO = pedidoDAO;
        this.repartidorDAO = repartidorDAO;
        initUI();
        cargarCombos();
        cargarDatosTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel Izquierdo: Formulario
        JPanel panelFormulario = new JPanel(new BorderLayout(5, 5));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Datos de la Entrega"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panelFormulario.setPreferredSize(new Dimension(360, 0));

        // Campos
        JPanel panelCampos = new JPanel(new GridLayout(11, 1, 4, 4));

        panelCampos.add(new JLabel("ID Entrega (Automático):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Pedido (*):"));
        cmbPedido = new JComboBox<>();
        panelCampos.add(cmbPedido);

        panelCampos.add(new JLabel("Repartidor (*):"));
        cmbRepartidor = new JComboBox<>();
        panelCampos.add(cmbRepartidor);

        panelCampos.add(new JLabel("Fecha (YYYY-MM-DD) (*):"));
        txtFecha = new JTextField(LocalDate.now().format(FORMATO_FECHA));
        panelCampos.add(txtFecha);

        panelCampos.add(new JLabel("Hora (HH:MM:SS) (*):"));
        txtHora = new JTextField(LocalTime.now().format(FORMATO_HORA));
        panelCampos.add(txtHora);

        btnAhora = new JButton("Usar Fecha y Hora Actual");
        panelCampos.add(btnAhora);

        panelFormulario.add(panelCampos, BorderLayout.NORTH);

        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 8));
        btnRegistrar = new JButton("Registrar Entrega");
        btnActualizar = new JButton("Actualizar Entrega");
        btnEliminar = new JButton("Eliminar Entrega");
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

        // Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelFiltros.setBorder(new TitledBorder("Filtros de Entregas"));

        panelFiltros.add(new JLabel("Por Repartidor:"));
        cmbFiltroRepartidor = new JComboBox<>();
        cmbFiltroRepartidor.setPreferredSize(new Dimension(160, 25));
        panelFiltros.add(cmbFiltroRepartidor);

        panelFiltros.add(new JLabel("Por Pedido:"));
        cmbFiltroPedido = new JComboBox<>();
        cmbFiltroPedido.setPreferredSize(new Dimension(180, 25));
        panelFiltros.add(cmbFiltroPedido);

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnRestablecerFiltros = new JButton("Restablecer");
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnRestablecerFiltros);

        panelCentral.add(panelFiltros, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "ID Pedido", "Dirección Pedido", "ID Repartidor", "Repartidor", "Fecha", "Hora"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEntregas = new JTable(modeloTabla);
        tablaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEntregas.getTableHeader().setReorderingAllowed(false);
        tablaEntregas.setRowHeight(22);

        JScrollPane scrollTabla = new JScrollPane(tablaEntregas);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Eventos
        configurarEventos(btnFiltrar, btnRestablecerFiltros);
    }

    private void configurarEventos(JButton btnFiltrar, JButton btnRestablecerFiltros) {
        btnAhora.addActionListener(e -> {
            txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
            txtHora.setText(LocalTime.now().format(FORMATO_HORA));
        });

        btnRegistrar.addActionListener(e -> registrarEntrega());
        btnActualizar.addActionListener(e -> actualizarEntrega());
        btnEliminar.addActionListener(e -> eliminarEntrega());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnRestablecerFiltros.addActionListener(e -> {
            if (cmbFiltroRepartidor.getItemCount() > 0) cmbFiltroRepartidor.setSelectedIndex(0);
            if (cmbFiltroPedido.getItemCount() > 0) cmbFiltroPedido.setSelectedIndex(0);
            cargarDatosTabla();
        });

        // Selección en tabla
        tablaEntregas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEntregas.getSelectedRow() != -1) {
                int fila = tablaEntregas.getSelectedRow();
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());

                int idPedido = Integer.parseInt(modeloTabla.getValueAt(fila, 1).toString());
                int idRepartidor = Integer.parseInt(modeloTabla.getValueAt(fila, 3).toString());
                txtFecha.setText(modeloTabla.getValueAt(fila, 5).toString());
                txtHora.setText(modeloTabla.getValueAt(fila, 6).toString());

                // Seleccionar en combos
                seleccionarPedidoEnCombo(idPedido);
                seleccionarRepartidorEnCombo(idRepartidor);

                btnRegistrar.setEnabled(false);
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });
    }

    /**
     * Refresca los JComboBox de Pedidos y Repartidores desde la base de datos.
     * Es invocado cuando se crea, edita o elimina un pedido o repartidor.
     */
    public void cargarCombos() {
        try {
            // Guardar selecciones actuales
            Pedido pedidoSeleccionado = (Pedido) cmbPedido.getSelectedItem();
            Repartidor repartidorSeleccionado = (Repartidor) cmbRepartidor.getSelectedItem();

            // Cargar Repartidores
            List<Repartidor> repartidores = repartidorDAO.readAll();
            cmbRepartidor.removeAllItems();
            cmbFiltroRepartidor.removeAllItems();
            cmbFiltroRepartidor.addItem("TODOS");

            for (Repartidor r : repartidores) {
                cmbRepartidor.addItem(r);
                cmbFiltroRepartidor.addItem(r);
            }

            // Cargar Pedidos
            List<Pedido> pedidos = pedidoDAO.readAll();
            cmbPedido.removeAllItems();
            cmbFiltroPedido.removeAllItems();
            cmbFiltroPedido.addItem("TODOS");

            for (Pedido p : pedidos) {
                cmbPedido.addItem(p);
                cmbFiltroPedido.addItem(p);
            }

            // Restaurar selecciones si aún existen
            if (pedidoSeleccionado != null) {
                seleccionarPedidoEnCombo(pedidoSeleccionado.getId());
            }
            if (repartidorSeleccionado != null) {
                seleccionarRepartidorEnCombo(repartidorSeleccionado.getId());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al sincronizar combos desde la base de datos: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarPedidoEnCombo(int idPedido) {
        for (int i = 0; i < cmbPedido.getItemCount(); i++) {
            Pedido p = cmbPedido.getItemAt(i);
            if (p.getId() == idPedido) {
                cmbPedido.setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarRepartidorEnCombo(int idRepartidor) {
        for (int i = 0; i < cmbRepartidor.getItemCount(); i++) {
            Repartidor r = cmbRepartidor.getItemAt(i);
            if (r.getId() == idRepartidor) {
                cmbRepartidor.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Valida y registra una nueva entrega en la BD.
     */
    private void registrarEntrega() {
        Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cmbRepartidor.getSelectedItem();

        if (pedido == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un Pedido. (Si no hay pedidos registrados, cree uno primero).",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (repartidor == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un Repartidor. (Si no hay repartidores registrados, cree uno primero).",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fecha = parsearFecha(txtFecha.getText().trim());
        if (fecha == null) return;

        LocalTime hora = parsearHora(txtHora.getText().trim());
        if (hora == null) return;

        try {
            Entrega entrega = new Entrega(pedido.getId(), repartidor.getId(), fecha, hora);
            int idGenerado = entregaDAO.create(entrega);

            if (idGenerado > 0) {
                JOptionPane.showMessageDialog(this,
                        "Entrega #" + idGenerado + " registrada exitosamente.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar la entrega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al registrar entrega: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida y actualiza una entrega existente.
     */
    private void actualizarEntrega() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una entrega de la tabla para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor = (Repartidor) cmbRepartidor.getSelectedItem();

        if (pedido == null || repartidor == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un Pedido y un Repartidor válidos.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fecha = parsearFecha(txtFecha.getText().trim());
        if (fecha == null) return;

        LocalTime hora = parsearHora(txtHora.getText().trim());
        if (hora == null) return;

        try {
            int id = Integer.parseInt(txtId.getText());
            Entrega entrega = new Entrega(id, pedido.getId(), repartidor.getId(), fecha, hora);

            boolean exito = entregaDAO.update(entrega);
            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Entrega #" + id + " actualizada correctamente.",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarDatosTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la entrega para actualizar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al actualizar la entrega: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Confirma y elimina la entrega seleccionada.
     */
    private void eliminarEntrega() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una entrega de la tabla para eliminar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el registro de la entrega #" + id + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = entregaDAO.delete(id);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                            "Entrega eliminada exitosamente.",
                            "Operación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarDatosTabla();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar la entrega.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error de base de datos al eliminar entrega: " + ex.getMessage(),
                        "Error SQL",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga todas las entregas desde la BD a la tabla.
     */
    public void cargarDatosTabla() {
        try {
            List<Entrega> entregas = entregaDAO.readAll();
            llenarTabla(entregas);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar entregas: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filtra entregas por Repartidor y/o Pedido según selección en combos de filtro.
     */
    private void aplicarFiltros() {
        Integer idRepartidor = null;
        Integer idPedido = null;

        Object itemRepartidor = cmbFiltroRepartidor.getSelectedItem();
        if (itemRepartidor instanceof Repartidor) {
            idRepartidor = ((Repartidor) itemRepartidor).getId();
        }

        Object itemPedido = cmbFiltroPedido.getSelectedItem();
        if (itemPedido instanceof Pedido) {
            idPedido = ((Pedido) itemPedido).getId();
        }

        try {
            List<Entrega> entregas = entregaDAO.readByFiltros(idPedido, idRepartidor);
            llenarTabla(entregas);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al filtrar entregas: " + ex.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarTabla(List<Entrega> entregas) {
        modeloTabla.setRowCount(0);
        for (Entrega e : entregas) {
            modeloTabla.addRow(new Object[]{
                    e.getId(),
                    e.getIdPedido(),
                    e.getDireccionPedido() != null ? e.getDireccionPedido() : "Pedido #" + e.getIdPedido(),
                    e.getIdRepartidor(),
                    e.getNombreRepartidor() != null ? e.getNombreRepartidor() : "Repartidor #" + e.getIdRepartidor(),
                    e.getFecha().toString(),
                    e.getHora().toString()
            });
        }
    }

    private LocalDate parsearFecha(String str) {
        try {
            return LocalDate.parse(str, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Utilice el formato YYYY-MM-DD (Ejemplo: 2026-10-05).",
                    "Error de Formato",
                    JOptionPane.WARNING_MESSAGE);
            txtFecha.requestFocus();
            return null;
        }
    }

    private LocalTime parsearHora(String str) {
        try {
            if (str.length() == 5) {
                return LocalTime.parse(str, FORMATO_HORA_CORTA);
            }
            return LocalTime.parse(str, FORMATO_HORA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de hora inválido. Utilice el formato HH:MM o HH:MM:SS (Ejemplo: 14:30:00).",
                    "Error de Formato",
                    JOptionPane.WARNING_MESSAGE);
            txtHora.requestFocus();
            return null;
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        if (cmbPedido.getItemCount() > 0) cmbPedido.setSelectedIndex(0);
        if (cmbRepartidor.getItemCount() > 0) cmbRepartidor.setSelectedIndex(0);
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        txtHora.setText(LocalTime.now().format(FORMATO_HORA));
        tablaEntregas.clearSelection();
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
}
