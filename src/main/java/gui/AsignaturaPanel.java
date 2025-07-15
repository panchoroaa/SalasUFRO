package gui;

import controlador.AsignacionControlador;
import modelo.Asignatura;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AsignaturaPanel extends JPanel {

    private AsignacionControlador controlador;
    private JTable asignaturaTable;
    private DefaultTableModel tableModel;

    public AsignaturaPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        initComponents();
        loadAsignaturas();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Gestión de Asignaturas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Eliminamos "Créditos" de los nombres de columna
        String[] columnNames = {"Código", "Nombre", "Cantidad Alumnos"}; // <--- CAMBIO AQUÍ
        tableModel = new DefaultTableModel(columnNames, 0);
        asignaturaTable = new JTable(tableModel);
        asignaturaTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(asignaturaTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadAsignaturas() {
        tableModel.setRowCount(0);
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        for (Asignatura a : asignaturas) {
            // Eliminamos a.getCreditos() de los datos de la fila
            Object[] rowData = {a.getCodigo(), a.getNombre(), a.getCantidadAlumnos()}; // <--- CAMBIO AQUÍ
            tableModel.addRow(rowData);
        }
    }
}