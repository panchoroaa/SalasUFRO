// Ubicación: gui/DialogoDetalleAsignacion.java
package gui;

import controlador.AsignacionControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Optional;

public class DialogoDetalleAsignacion extends JDialog {
    private AsignacionControlador controlador;
    private Reserva reserva;
    private boolean cancelacionExitosa = false;

    public DialogoDetalleAsignacion(JFrame parent, AsignacionControlador controlador, Reserva reserva) {
        super(parent, "Detalle de Asignación", true);
        this.controlador = controlador;
        this.reserva = reserva;

        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        Horario h = reserva.getHorario();
        Optional<Profesor> profOpt = controlador.getProfesorPorRut(reserva.getRutProfesor());
        Optional<Asignatura> asigOpt = controlador.getAsignaturaPorCodigo(reserva.getCodigoAsignatura());
        Optional<Sala> salaOpt = controlador.getSalaPorNombre(reserva.getNombreSala());


        String dia = h.getDia().toString();
        String periodo = h.getBloqueEnum().getNumeroBloque() + "º";
        String horaIni = h.getBloqueEnum().getHoraInicio();
        String horaTer = h.getBloqueEnum().getHoraFin();
        String asignatura = asigOpt.map(a -> a.getNombre() + " (" + a.getCodigo() + ")").orElse("N/A");
        String sala = salaOpt.map(Sala::getNombre).orElse("N/A");
        String profesor = profOpt.map(Profesor::getNombre).orElse("N/A");


        detailsPanel.add(createDetailRow("Día:", dia));
        detailsPanel.add(createDetailRow("Periodo:", periodo));
        detailsPanel.add(createDetailRow("Horario:", horaIni + " - " + horaTer));
        detailsPanel.add(createDetailRow("Asignatura:", asignatura));
        detailsPanel.add(createDetailRow("Sala:", sala));
        detailsPanel.add(createDetailRow("Profesor:", profesor));

        add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton cancelarButton = new JButton("Cancelar Asignación");
        JButton salirButton = new JButton("Salir");

        buttonPanel.add(cancelarButton);
        buttonPanel.add(salirButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        salirButton.addActionListener(e -> dispose());

        cancelarButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea cancelar esta asignación?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String resultado = controlador.cancelarAsignacion(reserva);
                JOptionPane.showMessageDialog(this, resultado);
                if (!resultado.startsWith("Error")) {
                    this.cancelacionExitosa = true;
                    dispose();
                }
            }
        });
    }


    private JPanel createDetailRow(String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        labelComponent.setPreferredSize(new Dimension(100, 20));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        return rowPanel;
    }

    public boolean isCancelacionExitosa() {
        return cancelacionExitosa;
    }
}