package gui;

import modelo.Reserva;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ReservaTableModel extends AbstractTableModel {
    private List<Reserva> reservas;
    private final String[] columnNames = {"ID", "Profesor (RUT)", "Asignatura (Código)", "Día", "Bloque", "Sala"};

    public ReservaTableModel() {
        this.reservas = new ArrayList<>();
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = new ArrayList<>(reservas);
        fireTableDataChanged();
    }

    public Reserva getReservaAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < reservas.size()) {
            return reservas.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return reservas.size();
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
        Reserva reserva = reservas.get(rowIndex);
        switch (columnIndex) {
            case 0: return rowIndex + 1;
            case 1: return reserva.getRutProfesor();
            case 2: return reserva.getCodigoAsignatura();
            case 3: return reserva.getHorario().getDia().toString();
            case 4: return reserva.getHorario().getBloqueEnum().toString();
            case 5: return reserva.getNombreSala();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        return String.class;
    }
}