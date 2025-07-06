package modelo;

import java.util.ArrayList;
import java.util.List;

public class GestionHorarios {
    private final List<Horario> horariosOcupados;

    public GestionHorarios() {
        this.horariosOcupados = new ArrayList<>();
    }

    public boolean estaDisponible(Horario horario) {
        if (horario == null) {
            return false;
        }
        return horariosOcupados.stream()
                .noneMatch(h -> h.equals(horario));
    }

    public void agregarHorario(Horario horario) {
        if (horario != null && estaDisponible(horario)) {
            horariosOcupados.add(horario);
        }
    }

    public void eliminarHorario(Horario horario) {
        horariosOcupados.removeIf(h -> h.equals(horario));
    }

    public List<Horario> getHorariosOcupados() {
        return new ArrayList<>(horariosOcupados);
    }
}