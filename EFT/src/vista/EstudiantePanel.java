package vista;

import controlador.EstudianteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EstudiantePanel extends JPanel {
    private final EstudianteController controller;
    private JTextField txtId, txtNombre, txtRut, txtCurso, txtCorreo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;

    public EstudiantePanel() {
        controller = new EstudianteController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gestión de Estudiantes", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));
        panelForm.setPreferredSize(new Dimension(350, 0));

        JPanel panelCampos = new JPanel(new GridLayout(5, 2, 10, 15));
        panelCampos.setBorder(new EmptyBorder(20, 10, 20, 10));

        panelCampos.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("RUT:"));
        txtRut = new JTextField();
        panelCampos.add(txtRut);

        panelCampos.add(new JLabel("Curso:"));
        txtCurso = new JTextField();
        panelCampos.add(txtCurso);

        panelCampos.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        panelCampos.add(txtCorreo);

        panelForm.add(panelCampos, BorderLayout.CENTER);

        // Botones
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

        panelForm.add(panelBotones, BorderLayout.SOUTH);
        add(panelForm, BorderLayout.WEST);

        // Tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lista de Estudiantes", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        String[] columnas = {"ID", "Nombre", "RUT", "Curso", "Correo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // Eventos
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
            List<Estudiante> estudiantes = controller.listarTodos();
            for (Estudiante e : estudiantes) {
                modeloTabla.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tabla.clearSelection();
    }

    private void cargarDatosSeleccionados() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtRut.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtCurso.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());
        
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void registrar() {
        try {
            Estudiante e = new Estudiante();
            e.setNombre(txtNombre.getText());
            e.setRut(txtRut.getText());
            e.setCurso(txtCurso.getText());
            e.setCorreo(txtCorreo.getText());
            controller.registrar(e);
            JOptionPane.showMessageDialog(this, "Registrado.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizar() {
        try {
            Estudiante e = new Estudiante();
            e.setId(Integer.parseInt(txtId.getText()));
            e.setNombre(txtNombre.getText());
            e.setRut(txtRut.getText());
            e.setCurso(txtCurso.getText());
            e.setCorreo(txtCorreo.getText());
            controller.actualizar(e);
            JOptionPane.showMessageDialog(this, "Actualizado.");
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
                JOptionPane.showMessageDialog(this, "Eliminado.");
                limpiarFormulario();
                cargarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
