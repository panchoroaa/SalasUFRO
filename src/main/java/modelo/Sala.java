package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Necesario para equals y hashCode

public class Sala {
    private String nombre;
    private int capacidad;
    private String estado;
    private List<Horario> horariosOcupados; // Lista de horarios en los que la sala está ocupada

    // Constructor vacío para Jackson (importante para deserialización)
    public Sala() {
        this.horariosOcupados = new ArrayList<>();
    }

    public Sala(String nombre, int capacidad, String estado) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = estado;
        this.horariosOcupados = new ArrayList<>(); // Inicializar la lista
    }

    // Constructor que acepta la lista de horarios directamente
    public Sala(String nombre, int capacidad, String estado, List<Horario> horariosOcupados) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = estado;
        this.horariosOcupados = (horariosOcupados != null) ? new ArrayList<>(horariosOcupados) : new ArrayList<>();
    }


    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Horario> getHorariosOcupados() {
        return new ArrayList<>(horariosOcupados); // Devolver una copia para proteger la lista interna
    }

    public void setHorariosOcupados(List<Horario> horariosOcupados) {
        this.horariosOcupados = (horariosOcupados != null) ? new ArrayList<>(horariosOcupados) : new ArrayList<>();
    }

    // Métodos para gestionar horarios ocupados
    public void agregarHorarioOcupado(Horario horario) {
        if (horario != null && !this.horariosOcupados.contains(horario)) {
            this.horariosOcupados.add(horario);
        }
    }

    // ** NUEVO MÉTODO **
    public void eliminarHorarioOcupado(Horario horario) {
        if (horario != null) {
            // Usa removeIf para eliminar el horario que sea "igual" (según el método equals de Horario)
            this.horariosOcupados.removeIf(h -> h.equals(horario));
        }
    }


    public boolean estaDisponible(Horario horarioDeseado) {
        if (horarioDeseado == null) {
            return false; // Un horario nulo no es válido
        }
        for (Horario ocupado : this.horariosOcupados) {
            if (ocupado.equals(horarioDeseado)) { // Asume que Horario.equals() compara día y bloque
                return false; // La sala ya está ocupada en este horario
            }
        }
        return true; // La sala está disponible
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sala {");
        sb.append("nombre='").append(nombre).append('\'');
        sb.append(", capacidad=").append(capacidad);
        sb.append(", estado='").append(estado).append('\'');
        sb.append(", horariosOcupados=[");
        if (horariosOcupados != null && !horariosOcupados.isEmpty()) {
            List<String> horariosStr = new ArrayList<>();
            for (Horario h : horariosOcupados) {
                if (h != null) { // Evitar NullPointerException si hay un horario nulo en la lista
                    horariosStr.add(String.format("Día: %s, Bloque: %s - %s",
                            h.getDia(), h.getBloque().getHoraInicio(), h.getBloque().getHoraFin()));
                }
            }
            sb.append(String.join("; ", horariosStr));
        } else {
            sb.append("Ninguno");
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala sala = (Sala) o;
        return Objects.equals(nombre, sala.nombre); // Una sala es igual si tiene el mismo nombre
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}