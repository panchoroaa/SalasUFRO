package gui;

import controlador.AsignacionControlador;
import modelo.Sala; // Asegúrate de que modelo.Sala esté importado

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalaPanel extends JPanel {

    private AsignacionControlador controlador;
    private JTable salaTable;
    private DefaultTableModel tableModel;

    public SalaPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        initComponents();
        loadSalas();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Salas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Definimos las columnas de la tabla. Cambiado "Tipo" por "Estado"
        String[] columnNames = {"Nombre", "Capacidad", "Estado"}; // <--- CAMBIO AQUÍ
        tableModel = new DefaultTableModel(columnNames, 0);
        salaTable = new JTable(tableModel);
        salaTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(salaTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadSalas() {
        tableModel.setRowCount(0);
        List<Sala> salas = controlador.getSalas();
        for (Sala s : salas) {
            // Usamos s.getEstado().name() para obtener el String del enum EstadoSala
            Object[] rowData = {s.getNombre(), s.getCapacidad(), s.getEstado().name()}; // <--- CAMBIO AQUÍ
            tableModel.addRow(rowData);
        }
    }
}