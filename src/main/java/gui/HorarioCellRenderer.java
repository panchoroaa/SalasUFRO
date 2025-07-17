// Ubicación: gui/HorarioCellRenderer.java
package gui;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Optional;

public class HorarioCellRenderer extends DefaultTableCellRenderer {
    private AsignacionControlador controlador;

    public HorarioCellRenderer(AsignacionControlador controlador) {
        this.controlador = controlador;
        setVerticalAlignment(SwingConstants.TOP);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Reserva) {
            Reserva reserva = (Reserva) value;

            Optional<Asignatura> asigOpt = controlador.getAsignaturaPorCodigo(reserva.getCodigoAsignatura());
            Optional<Profesor> profOpt = controlador.getProfesorPorRut(reserva.getRutProfesor());
            Optional<Sala> salaOpt = controlador.getSalaPorNombre(reserva.getNombreSala());

            String codigoAsignatura = asigOpt.map(Asignatura::getCodigo).orElse("N/A");
            String nombreProfesor = profOpt.map(p -> p.getNombre().split(" ")[0]).orElse("N/A");
            String nombreSala = salaOpt.map(Sala::getNombre).orElse("N/A");

            setText(String.format("<html><div style='text-align: center;'><b>%s</b><br>%s<br>%s</div></html>",
                    codigoAsignatura, nombreProfesor, nombreSala));
            setBackground(new Color(220, 237, 255)); // Un color celeste claro para las celdas ocupadas
            setForeground(Color.BLACK);
        } else {
            setText("");
            setBackground(Color.WHITE);
        }

        if (column == 0) {
            setBackground(new Color(240, 240, 240));
            setFont(getFont().deriveFont(Font.BOLD));
            setText(value.toString());
        }

        return this;
    }
}