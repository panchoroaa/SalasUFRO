package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GestionSala implements Serializable {
    private static final long serialVersionUID = 1L;
    private Sala sala;
    private EstadoSala estado;
    private List<Horario> horariosOcupados;

    public GestionSala(Sala sala) {
        this.sala = sala;
        this.estado = EstadoSala.DISPONIBLE;
        this.horariosOcupados = new ArrayList<>();
    }

    public GestionSala() {
        this.horariosOcupados = new ArrayList<>();
        this.estado = EstadoSala.DISPONIBLE; // Asegurar estado por defecto si se carga sin él
    }

    public Sala getSala() {
        return sala;
    }

    // Importante: Setter para Jackson cuando deserializa
    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public EstadoSala getEstado() {
        return estado;
    }

    public void setEstado(EstadoSala estado) {
        this.estado = estado;
    }

    public List<Horario> getHorariosOcupados() {
        return horariosOcupados;
    }

    // Importante: Setter para Jackson cuando deserializa
    public void setHorariosOcupados(List<Horario> horariosOcupados) {
        this.horariosOcupados = new ArrayList<>(horariosOcupados);
    }

    public boolean estaDisponible(Horario horario) {
        if (this.estado != EstadoSala.DISPONIBLE) {
            return false;
        }
        return horariosOcupados.stream().noneMatch(h -> h.equals(horario));
    }

    public void agregarHorarioOcupado(Horario horario) {
        if (!estaDisponible(horario)) {
            // Usa horario.getBloque().toString() aquí
            throw new IllegalStateException("La sala ya está ocupada o no disponible en el horario " + horario.getDia() + " " + horario.getBloque().toString());
        }
        horariosOcupados.add(horario);
    }

    public void removerHorarioOcupado(Horario horario) {
        if (!horariosOcupados.remove(horario)) {
            // Usa horario.getBloque().toString() aquí
            throw new IllegalArgumentException("El horario " + horario.getDia() + " " + horario.getBloque().toString() + " no estaba ocupado en esta sala.");
        }
    }

    public boolean tieneHorarioOcupado(Horario horario) {
        return horariosOcupados.contains(horario);
    }

    @Override
    public String toString() {
        return "GestionSala{" +
                "sala=" + (sala != null ? sala.getNombre() : "N/A") +
                ", estado=" + estado +
                ", horariosOcupados=" + horariosOcupados.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GestionSala that = (GestionSala) o;
        return sala.equals(that.sala);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sala);
    }
}