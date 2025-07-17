// Ubicación: gui/HorarioVisualTableModel.java
package gui;

import modelo.BloqueHorario;
import modelo.DiaSemana;
import modelo.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class HorarioVisualTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Periodo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
    private final Object[][] data;

    public HorarioVisualTableModel() {
        this.data = new Object[BloqueHorario.values().length][columnNames.length];
    }

    public void setReservas(List<Reserva> reservas) {
        // Limpiar datos antiguos
        for (int i = 0; i < data.length; i++) {
            for (int j = 1; j < data[i].length; j++) {
                data[i][j] = null;
            }
        }


        for (Reserva r : reservas) {
            int row = r.getHorario().getBloqueEnum().ordinal();
            int col = r.getHorario().getDia().ordinal() + 1;
            if (row < getRowCount() && col < getColumnCount()) {
                data[row][col] = r;
            }
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return BloqueHorario.values().length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            BloqueHorario bloque = BloqueHorario.values()[rowIndex];
            return String.format("<html>%s<br>%s - %s</html>", bloque.getNumeroBloque() + "º", bloque.getHoraInicio(), bloque.getHoraFin());
        }
        return data[rowIndex][columnIndex];
    }
}