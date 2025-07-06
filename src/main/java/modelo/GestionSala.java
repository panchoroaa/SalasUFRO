// modelo/GestionSala.java
package modelo;

import java.util.List;
import java.util.Objects;

public class GestionSala {
    private final Sala sala; // La sala informativa que esta gestión complementa
    private final GestionHorarios gestionHorarios;
    private EstadoSala estado;

    public GestionSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("La sala asociada no puede ser nula.");
        }
        this.sala = sala;
        this.gestionHorarios = new GestionHorarios(); // Gestiona sus propios horarios
        this.estado = EstadoSala.DISPONIBLE; // Estado inicial por defecto
    }

    public Sala getSala() {
        return sala;
    }

    public EstadoSala getEstado() {
        return estado;
    }

    public void setEstado(EstadoSala estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public boolean estaDisponible(Horario horario) {
        return estado == EstadoSala.DISPONIBLE && gestionHorarios.estaDisponible(horario);
    }

    public void agregarHorarioOcupado(Horario horario) {
        if (estado != EstadoSala.DISPONIBLE) {
            throw new IllegalStateException("No se pueden agregar horarios a una sala en estado " + estado.toString().toLowerCase() + ".");
        }
        if (!gestionHorarios.estaDisponible(horario)) {
            throw new IllegalStateException("El horario " + horario + " ya está ocupado para la sala " + sala.getNombre() + ".");
        }
        gestionHorarios.agregarHorario(horario);
    }

    public void eliminarHorarioOcupado(Horario horario) {
        gestionHorarios.eliminarHorario(horario);
    }

    public List<Horario> getHorariosOcupados() {
        return gestionHorarios.getHorariosOcupados();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GestionSala that = (GestionSala) o;
        return Objects.equals(sala, that.sala); // Dos GestionSala son iguales si gestionan la misma Sala informativa
    }

    @Override
    public int hashCode() {
        return Objects.hash(sala);
    }
}