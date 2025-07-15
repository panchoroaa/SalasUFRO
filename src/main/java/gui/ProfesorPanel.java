package gui;

import controlador.AsignacionControlador;
import modelo.Profesor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfesorPanel extends JPanel {

    private AsignacionControlador controlador;
    private JTable profesorTable;
    private DefaultTableModel tableModel;

    public ProfesorPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        initComponents();
        loadProfesores();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Gestión de Profesores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Nombre", "RUT", "Departamento"};
        tableModel = new DefaultTableModel(columnNames, 0);
        profesorTable = new JTable(tableModel);
        profesorTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(profesorTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadProfesores() {
        tableModel.setRowCount(0);
        List<Profesor> profesores = controlador.getProfesores();
        for (Profesor p : profesores) {
            Object[] rowData = {p.getNombre(), p.getRut(), p.getDepartamento()};
            tableModel.addRow(rowData);
        }
    }
}