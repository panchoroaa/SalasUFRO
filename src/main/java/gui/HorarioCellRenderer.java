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

            // Obtenemos la información necesaria
            Optional<Asignatura> asigOpt = controlador.getAsignaturaPorCodigo(reserva.getCodigoAsignatura());
            Optional<Sala> salaOpt = controlador.getSalaPorNombre(reserva.getNombreSala());

            // Preparamos las variables para mostrar
            String codigoAsignatura = asigOpt.map(Asignatura::getCodigo).orElse("N/A");
            String nombreAsignatura = asigOpt.map(Asignatura::getNombre).orElse("Asignatura Desconocida"); // <-- CAMBIO CLAVE
            String nombreSala = salaOpt.map(Sala::getNombre).orElse("N/A");

            // Construimos el texto con el NOMBRE DE LA ASIGNATURA en el medio
            setText(String.format("<html><div style='text-align: center;'><b>%s</b><br>%s<br>%s</div></html>",
                    codigoAsignatura, nombreAsignatura, nombreSala));

            setBackground(new Color(220, 237, 255));
            setForeground(Color.BLACK);

        } else {
            setText(""); // Celda vacía
            setBackground(Color.WHITE);
        }

        if (column == 0) { // Columna de los bloques horarios
            setBackground(new Color(240, 240, 240));
            setFont(getFont().deriveFont(Font.BOLD));
            setText(value.toString());
        }

        return this;
    }
}