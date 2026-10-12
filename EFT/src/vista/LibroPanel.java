package vista;

import controlador.LibroController;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibroPanel extends JPanel {
    private final LibroController controller;
    private JTextField txtId, txtTitulo, txtAutor, txtIsbn, txtEditorial;
    private JSpinner spinStock;
    private JComboBox<Categoria> cmbCategoria;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;

    public LibroPanel() {
        controller = new LibroController();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulario (Izquierda)
        JPanel panelForm = new JPanel(new BorderLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gestión de Libros", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));
        panelForm.setPreferredSize(new Dimension(350, 0));

        JPanel panelCampos = new JPanel(new GridLayout(7, 2, 10, 15));
        panelCampos.setBorder(new EmptyBorder(20, 10, 20, 10));

        panelCampos.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        panelCampos.add(txtTitulo);

        panelCampos.add(new JLabel("Autor:"));
        txtAutor = new JTextField();
        panelCampos.add(txtAutor);

        panelCampos.add(new JLabel("ISBN:"));
        txtIsbn = new JTextField();
        panelCampos.add(txtIsbn);

        panelCampos.add(new JLabel("Editorial:"));
        txtEditorial = new JTextField();
        panelCampos.add(txtEditorial);

        panelCampos.add(new JLabel("Stock:"));
        spinStock = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        panelCampos.add(spinStock);

        panelCampos.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cargarCategorias();
        panelCampos.add(cmbCategoria);

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

        // Tabla (Derecha)
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lista de Libros", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        panelTabla.add(scrollPane, BorderLayout.CENTER);
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

    private void cargarCategorias() {
        try {
            List<Categoria> categorias = controller.listarCategorias();
            cmbCategoria.removeAllItems();
            for (Categoria c : categorias) cmbCategoria.addItem(c);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Libro> libros = controller.listarTodos();
            for (Libro l : libros) {
                modeloTabla.addRow(new Object[]{
                    l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                    l.getEditorial(), l.getStock(), l.getNombreCategoria()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        spinStock.setValue(1);
        if (cmbCategoria.getItemCount() > 0) cmbCategoria.setSelectedIndex(0);
        
        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tabla.clearSelection();
    }

    private void cargarDatosSeleccionados() {
        int fila = tabla.getSelectedRow();
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtTitulo.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtAutor.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtIsbn.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtEditorial.setText(modeloTabla.getValueAt(fila, 4).toString());
        spinStock.setValue(Integer.parseInt(modeloTabla.getValueAt(fila, 5).toString()));
        
        Object catObj = modeloTabla.getValueAt(fila, 6);
        String catNombre = catObj != null ? catObj.toString() : "";
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getNombre().equals(catNombre)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
        
        btnRegistrar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void registrar() {
        try {
            Libro l = new Libro();
            l.setTitulo(txtTitulo.getText());
            l.setAutor(txtAutor.getText());
            l.setIsbn(txtIsbn.getText());
            l.setEditorial(txtEditorial.getText());
            l.setStock((Integer) spinStock.getValue());
            Categoria c = (Categoria) cmbCategoria.getSelectedItem();
            if (c != null) l.setIdCategoria(c.getId());

            controller.registrar(l);
            JOptionPane.showMessageDialog(this, "Registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizar() {
        try {
            Libro l = new Libro();
            l.setId(Integer.parseInt(txtId.getText()));
            l.setTitulo(txtTitulo.getText());
            l.setAutor(txtAutor.getText());
            l.setIsbn(txtIsbn.getText());
            l.setEditorial(txtEditorial.getText());
            l.setStock((Integer) spinStock.getValue());
            Categoria c = (Categoria) cmbCategoria.getSelectedItem();
            if (c != null) l.setIdCategoria(c.getId());

            controller.actualizar(l);
            JOptionPane.showMessageDialog(this, "Actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminar() {
        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar libro ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.eliminar(id);
                JOptionPane.showMessageDialog(this, "Eliminado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
