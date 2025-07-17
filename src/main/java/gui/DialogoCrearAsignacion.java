// Ubicación: gui/DialogoCrearAsignacion.java
package gui;

import controlador.AsignacionControlador;
import modelo.*;

import javax.swing.*;
import java.awt.*;

public class DialogoCrearAsignacion extends JDialog {
    private AsignacionControlador controlador;
    private DiaSemana dia;
    private BloqueHorario bloque;
    private boolean asignacionCreada = false;

    private JComboBox<Asignatura> asignaturaComboBox;
    private JComboBox<Profesor> profesorComboBox;
    private JComboBox<Sala> salaComboBox;

    public DialogoCrearAsignacion(JFrame parent, AsignacionControlador controlador, DiaSemana dia, BloqueHorario bloque) {
        super(parent, "Crear Asignación", true);
        this.controlador = controlador;
        this.dia = dia;
        this.bloque = bloque;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel(String.format("Horario: %s - %s", dia, bloque)), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Asignatura:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        asignaturaComboBox = new JComboBox<>();
        formPanel.add(asignaturaComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Profesor:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        profesorComboBox = new JComboBox<>();
        formPanel.add(profesorComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sala:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        salaComboBox = new JComboBox<>();
        formPanel.add(salaComboBox, gbc);

        JButton crearButton = new JButton("Crear Asignación");
        crearButton.addActionListener(e -> crearAsignacion());

        add(formPanel, BorderLayout.CENTER);
        add(crearButton, BorderLayout.SOUTH);

        // Listeners para actualizar dinámicamente
        asignaturaComboBox.addActionListener(e -> updateAvailableResources());
    }

    private void loadInitialData() {
        controlador.getAsignaturas().forEach(asignaturaComboBox::addItem);
        if (asignaturaComboBox.getItemCount() > 0) {
            asignaturaComboBox.setSelectedIndex(0);
        }
        updateAvailableResources();
    }

    private void updateAvailableResources() {
        profesorComboBox.removeAllItems();
        salaComboBox.removeAllItems();

        Asignatura selectedAsignatura = (Asignatura) asignaturaComboBox.getSelectedItem();
        if (selectedAsignatura == null) return;

        Horario horario = new Horario(dia, bloque);

        controlador.getProfesoresDisponibles(selectedAsignatura, horario)
                .forEach(profesorComboBox::addItem);

        controlador.getSalasDisponiblesEnHorario(horario).stream()
                .filter(s -> s.getCapacidad() >= selectedAsignatura.getCantidadAlumnos())
                .forEach(salaComboBox::addItem);
    }

    private void crearAsignacion() {
        Profesor prof = (Profesor) profesorComboBox.getSelectedItem();
        Sala sala = (Sala) salaComboBox.getSelectedItem();
        Asignatura asig = (Asignatura) asignaturaComboBox.getSelectedItem();

        if (prof == null || sala == null || asig == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Horario horario = new Horario(dia, bloque);
        String resultado = controlador.crearAsignacion(prof.getRut(), sala.getNombre(), asig.getCodigo(), horario);

        if (resultado.startsWith("Error")) {
            JOptionPane.showMessageDialog(this, resultado, "Error de Asignación", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.asignacionCreada = true;
            dispose();
        }
    }

    public boolean isAsignacionCreada() {
        return asignacionCreada;
    }
}