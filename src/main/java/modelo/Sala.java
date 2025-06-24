package modelo;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    private String nombre;
    private int capacidad;
    // The 'estado' field might become less critical for granular availability,
    // as availability is now determined by the 'horariosOcupados' list.
    // However, we can keep it for a general "Disponible" or "Mantenimiento" state if needed.
    private String estado;
    // NEW: List to store all occupied time slots for this sala
    private List<Horario> horariosOcupados;

    public Sala(String nombre, int capacidad, String estado, List<Horario> horariosOcupados) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = estado; // e.g., "Disponible", "Mantenimiento"
        this.horariosOcupados = (horariosOcupados != null) ? horariosOcupados : new ArrayList<>();
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public List<Horario> getHorariosOcupados() {
        return horariosOcupados;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // NEW: Method to add an occupied slot
    public void agregarHorarioOcupado(Horario horario) {
        if (horario != null && !horariosOcupados.contains(horario)) {
            this.horariosOcupados.add(horario);
        }
    }

    // NEW: Method to check if a sala is available for a specific horario
    public boolean estaDisponible(Horario horarioDeseado) {
        if (this.estado.equalsIgnoreCase("Mantenimiento") || this.estado.equalsIgnoreCase("Ocupada_General")) {
            return false; // If the sala has a general "unavailable" status
        }
        for (Horario ocupado : horariosOcupados) {
            if (ocupado.conflictuaCon(horarioDeseado)) {
                return false; // Conflicto de horario
            }
        }
        return true; // Disponible para ese horario específico
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sala{")
                .append("nombre='").append(nombre).append('\'')
                .append(", capacidad=").append(capacidad)
                .append(", estado='").append(estado).append('\'')
                .append(", horariosOcupados=");

        if (horariosOcupados.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            for (int i = 0; i < horariosOcupados.size(); i++) {
                sb.append(horariosOcupados.get(i).toString());
                if (i < horariosOcupados.size() - 1) {
                    sb.append("; ");
                }
            }
            sb.append("]");
        }
        sb.append('}');
        return sb.toString();
    }
}