package gui;

import controlador.AsignacionControlador;
import modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CancelarAsignacionPanel extends JPanel {

    private AsignacionControlador controlador;
    private JTable asignacionTable;
    private ReservaTableModel tableModel;
    private JButton cancelarButton;
    private JLabel messageLabel;

    public CancelarAsignacionPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        setupListeners();
        loadReservations();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Cancelar Asignación", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new ReservaTableModel();
        asignacionTable = new JTable(tableModel);
        asignacionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(asignacionTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cancelarButton = new JButton("Cancelar Asignación Seleccionada");
        southPanel.add(cancelarButton);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        southPanel.add(messageLabel);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        cancelarButton.addActionListener(e -> cancelarAsignacionSeleccionada());
    }

    public void loadReservations() {
        List<Reserva> reservas = controlador.getReservas();
        tableModel.setReservas(reservas);
        messageLabel.setText("");
    }

    private void cancelarAsignacionSeleccionada() {
        int selectedRow = asignacionTable.getSelectedRow();
        if (selectedRow == -1) {
            messageLabel.setText("Error: Por favor, seleccione una asignación para cancelar.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        Reserva reservaACancelar = tableModel.getReservaAt(selectedRow);
        if (reservaACancelar == null) {
            messageLabel.setText("Error: No se pudo obtener la asignación seleccionada.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea cancelar esta asignación?\n" + reservaACancelar.toString(),
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = controlador.cancelarAsignacion(reservaACancelar);
            if (resultado.startsWith("Error")) {
                messageLabel.setText(resultado);
                messageLabel.setForeground(Color.RED);
            } else {
                messageLabel.setText("¡Éxito! " + resultado);
                messageLabel.setForeground(new Color(34, 139, 34));
                loadReservations();
            }
        }
    }
}