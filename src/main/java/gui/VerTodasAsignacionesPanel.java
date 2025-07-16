package gui;

import controlador.AsignacionControlador;
import modelo.DiaSemana;
import modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VerTodasAsignacionesPanel extends JPanel {

    private AsignacionControlador controlador;
    private JTable asignacionTable;
    private ReservaTableModel tableModel;

    private JTextField filterProfesorField;
    private JTextField filterSalaField;
    private JComboBox<DiaSemana> filterDiaComboBox;
    private JButton filterButton;
    private JButton clearFilterButton;
    private JLabel filterMessageLabel;

    public VerTodasAsignacionesPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        setupListeners();
        loadReservations();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Visualizar y Filtrar Asignaciones", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Profesor (RUT/Nombre):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        filterProfesorField = new JTextField(15);
        filterPanel.add(filterProfesorField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        filterPanel.add(new JLabel("Sala (Nombre):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        filterSalaField = new JTextField(15);
        filterPanel.add(filterSalaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Día:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        filterDiaComboBox = new JComboBox<>();
        filterDiaComboBox.addItem(null);
        for (DiaSemana dia : DiaSemana.values()) {
            filterDiaComboBox.addItem(dia);
        }
        filterPanel.add(filterDiaComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 1;
        filterButton = new JButton("Aplicar Filtro");
        filterPanel.add(filterButton, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        clearFilterButton = new JButton("Mostrar Todas");
        filterPanel.add(clearFilterButton, gbc);

        filterMessageLabel = new JLabel("", SwingConstants.CENTER);
        filterMessageLabel.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        filterPanel.add(filterMessageLabel, gbc);

        add(filterPanel, BorderLayout.NORTH);

        tableModel = new ReservaTableModel();
        asignacionTable = new JTable(tableModel);
        asignacionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        asignacionTable.setAutoCreateRowSorter(true); // Habilitar ordenamiento
        JScrollPane scrollPane = new JScrollPane(asignacionTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupListeners() {
        filterButton.addActionListener(e -> applyFilters());
        clearFilterButton.addActionListener(e -> clearFilters());
    }

    public void loadReservations() {
        List<Reserva> allReservas = controlador.getReservas();
        tableModel.setReservas(allReservas);
        filterMessageLabel.setText("Mostrando todas las asignaciones.");
    }

    private void applyFilters() {
        String rutProfesor = filterProfesorField.getText().trim();
        String nombreSala = filterSalaField.getText().trim();
        DiaSemana selectedDia = (DiaSemana) filterDiaComboBox.getSelectedItem();

        String finalRutProfesor = rutProfesor.isEmpty() ? null : rutProfesor;
        String finalNombreSala = nombreSala.isEmpty() ? null : nombreSala;

        List<Reserva> filteredReservas = controlador.filtrarReservas(finalRutProfesor, finalNombreSala, selectedDia);
        tableModel.setReservas(filteredReservas);
        filterMessageLabel.setText("Mostrando " + filteredReservas.size() + " asignaciones filtradas.");
    }

    private void clearFilters() {
        filterProfesorField.setText("");
        filterSalaField.setText("");
        filterDiaComboBox.setSelectedItem(null);
        loadReservations();
    }
}