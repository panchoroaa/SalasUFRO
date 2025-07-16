package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sala {
    private String nombre;
    private int capacidad;
    private EstadoSala estado; // Now uses the simplified EstadoSala
    private List<Horario> horariosOcupados; // Now a list of Horario objects

    public Sala(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = EstadoSala.DISPONIBLE;
        this.horariosOcupados = new ArrayList<>();
    }
    // Constructor sin argumentos necesario para la deserialización con Jackson.
    public Sala() {
        this.horariosOcupados = new ArrayList<>();
    }

    // --- Getters y Setters ---
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public EstadoSala getEstado() { return estado; }
    public void setEstado(EstadoSala estado) { this.estado = estado; }

    public List<Horario> getHorariosOcupados() { return horariosOcupados; }
    public void setHorariosOcupados(List<Horario> horariosOcupados) { this.horariosOcupados = horariosOcupados; }


    public boolean estaDisponibleEn(Horario horario) {
        // Updated logic based on EstadoSala enum values
        if (this.estado != EstadoSala.DISPONIBLE) {
            return false;
        }
        return this.horariosOcupados.stream().noneMatch(h -> h.equals(horario));
    }

    public void agregarHorarioOcupado(Horario horario) {
        if (!horariosOcupados.contains(horario)) {
            horariosOcupados.add(horario);
        }
    }

    public void removerHorarioOcupado(Horario horario) {
        horariosOcupados.remove(horario);
    }

    @Override
    public String toString() {
        return String.format("Sala: %-15s | Capacidad: %-3d | Estado: %s", nombre, capacidad, estado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return Objects.equals(nombre, sala.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}