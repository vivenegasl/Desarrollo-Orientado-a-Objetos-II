package vista;

import controlador.CategoriaController;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoriaPanel extends JPanel {
    private final CategoriaController controller;
    private JTextField txtId, txtNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;

    public CategoriaPanel() {
        controller = new CategoriaController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gestión de Categorías", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));
        panelForm.setPreferredSize(new Dimension(350, 0));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 15));
        panelCampos.setBorder(new EmptyBorder(20, 10, 20, 10));

        panelCampos.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelForm.add(panelCampos, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelForm.add(panelBotones, BorderLayout.CENTER);
        add(panelForm, BorderLayout.WEST);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lista de Categorías", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        String[] columnas = {"ID", "Nombre"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                cargarDatosSeleccionados();
            }
        });

        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Categoria> categorias = controller.listarTodos();
            for (Categoria c : categorias) {
                modeloTabla.addRow(new Object[]{c.getId(), c.getNombre()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tabla.clearSelection();
    }

    private void cargarDatosSeleccionados() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void registrar() {
        try {
            Categoria c = new Categoria();
            c.setNombre(txtNombre.getText());
            controller.registrar(c);
            JOptionPane.showMessageDialog(this, "Registrada.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizar() {
        try {
            Categoria c = new Categoria();
            c.setId(Integer.parseInt(txtId.getText()));
            c.setNombre(txtNombre.getText());
            controller.actualizar(c);
            JOptionPane.showMessageDialog(this, "Actualizada.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminar() {
        int id = Integer.parseInt(txtId.getText());
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.eliminar(id);
                JOptionPane.showMessageDialog(this, "Eliminada.");
                limpiarFormulario();
                cargarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
