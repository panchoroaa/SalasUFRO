package gui;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.DiaSemana;
import modelo.Horario;
import modelo.Profesor;
import modelo.Sala;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CrearAsignacionPanel extends JPanel {

    private AsignacionControlador controlador;

    private JComboBox<Asignatura> asignaturaComboBox;
    private JComboBox<DiaSemana> diaComboBox;
    private JComboBox<BloqueHorario> bloqueComboBox;
    private JComboBox<Profesor> profesorComboBox;
    private JComboBox<Sala> salaComboBox;

    private JButton crearAsignacionButton;
    private JLabel messageLabel;

    public CrearAsignacionPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadInitialData();
        setupListeners();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Realizar Nueva Asignación", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Asignatura:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        asignaturaComboBox = new JComboBox<>();
        formPanel.add(asignaturaComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Día:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        diaComboBox = new JComboBox<>(DiaSemana.values());
        formPanel.add(diaComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Bloque Horario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        bloqueComboBox = new JComboBox<>(BloqueHorario.values());
        formPanel.add(bloqueComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Profesor:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        profesorComboBox = new JComboBox<>();
        formPanel.add(profesorComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Sala:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        salaComboBox = new JComboBox<>();
        formPanel.add(salaComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        crearAsignacionButton = new JButton("Crear Asignación");
        formPanel.add(crearAsignacionButton, gbc);

        gbc.gridy = 6;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadInitialData() {
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        for (Asignatura a : asignaturas) {
            asignaturaComboBox.addItem(a);
        }
    }

    private void setupListeners() {
        asignaturaComboBox.addActionListener(e -> updateAvailableResources());
        diaComboBox.addActionListener(e -> updateAvailableResources());
        bloqueComboBox.addActionListener(e -> updateAvailableResources());

        crearAsignacionButton.addActionListener(e -> createNewAsignacion());

        SwingUtilities.invokeLater(this::updateAvailableResources);
    }

    private void updateAvailableResources() {
        messageLabel.setText("");

        Asignatura selectedAsignatura = (Asignatura) asignaturaComboBox.getSelectedItem();
        DiaSemana selectedDia = (DiaSemana) diaComboBox.getSelectedItem();
        BloqueHorario selectedBloque = (BloqueHorario) bloqueComboBox.getSelectedItem();

        if (selectedAsignatura == null || selectedDia == null || selectedBloque == null) {
            profesorComboBox.removeAllItems();
            salaComboBox.removeAllItems();
            messageLabel.setText("Seleccione una Asignatura, Día y Bloque.");
            messageLabel.setForeground(Color.BLUE);
            return;
        }

        Horario selectedHorario = new Horario(selectedDia, selectedBloque);

        // Actualizar Profesores disponibles
        profesorComboBox.removeAllItems();
        List<Profesor> profesoresDisponibles = controlador.getProfesoresDisponibles(selectedAsignatura, selectedHorario);
        for (Profesor p : profesoresDisponibles) {
            profesorComboBox.addItem(p);
        }

        // Actualizar Salas disponibles
        salaComboBox.removeAllItems();
        List<Sala> salasDisponibles = controlador.getSalasDisponiblesEnHorario(selectedHorario);
        int requiredCapacity = selectedAsignatura.getCantidadAlumnos();
        salasDisponibles.stream()
                .filter(s -> s.getCapacidad() >= requiredCapacity)
                .forEach(salaComboBox::addItem);

        if (profesorComboBox.getItemCount() == 0) {
            messageLabel.setText("No hay profesores disponibles para esta asignatura y horario.");
            messageLabel.setForeground(Color.ORANGE);
        } else if (salaComboBox.getItemCount() == 0) {
            messageLabel.setText("No hay salas disponibles con capacidad y horario para esta asignación.");
            messageLabel.setForeground(Color.ORANGE);
        } else {
            messageLabel.setText("Recursos disponibles actualizados.");
            messageLabel.setForeground(Color.BLACK);
        }
    }

    private void createNewAsignacion() {
        Asignatura selectedAsignatura = (Asignatura) asignaturaComboBox.getSelectedItem();
        Profesor selectedProfesor = (Profesor) profesorComboBox.getSelectedItem();
        Sala selectedSala = (Sala) salaComboBox.getSelectedItem();
        DiaSemana selectedDia = (DiaSemana) diaComboBox.getSelectedItem();
        BloqueHorario selectedBloque = (BloqueHorario) bloqueComboBox.getSelectedItem();

        if (selectedAsignatura == null || selectedProfesor == null || selectedSala == null || selectedDia == null || selectedBloque == null) {
            messageLabel.setText("Error: Asegúrese de seleccionar una opción válida para todos los campos.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        Horario horario = new Horario(selectedDia, selectedBloque);

        String resultado = controlador.crearAsignacion(
                selectedProfesor.getRut(),
                selectedSala.getNombre(),
                selectedAsignatura.getCodigo(),
                horario
        );

        if (resultado.startsWith("Error")) {
            messageLabel.setText(resultado);
            messageLabel.setForeground(Color.RED);
        } else {
            messageLabel.setText("¡Éxito! " + resultado);
            messageLabel.setForeground(new Color(34, 139, 34));
        }
    }
}