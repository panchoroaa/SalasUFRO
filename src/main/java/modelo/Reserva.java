// modelo/Reserva.java
package modelo;

import java.util.Objects;

public class Reserva {
    private final Profesor profesor;
    private final Sala sala; // La sala informativa
    private final Asignatura asignatura;
    private final Horario horario;
    // Referencia a la GestionSala que maneja el estado y horarios reales de la sala.
    // Es 'transient' para que no se intente serializar directamente con GSON si GestionSala
    // se serializa por separado o se reconstruye de otra forma.
    private transient GestionSala gestionSalaAsociada;

    // Constructor principal usado por los controladores para crear una nueva reserva
    public Reserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario, GestionSala gestionSalaAsociada) {
        if (profesor == null) throw new IllegalArgumentException("El profesor no puede ser nulo.");
        if (sala == null) throw new IllegalArgumentException("La sala no puede ser nula.");
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula.");
        if (horario == null) throw new IllegalArgumentException("El horario no puede ser nulo.");
        if (gestionSalaAsociada == null) throw new IllegalArgumentException("La gestión de sala asociada no puede ser nula.");
        if (!gestionSalaAsociada.getSala().equals(sala)) {
            throw new IllegalArgumentException("La gestión de sala proporcionada no corresponde a la sala de la reserva.");
        }

        this.profesor = profesor;
        this.sala = sala;
        this.asignatura = asignatura;
        this.horario = horario;
        this.gestionSalaAsociada = gestionSalaAsociada;
    }

    // Constructor auxiliar, posiblemente para carga desde JSON donde gestionSalaAsociada
    // se reasocia después.
    public Reserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        this(profesor, sala, asignatura, horario, null); // Se asignará después
    }

    // Getters
    public Profesor getProfesor() { return profesor; }
    public Sala getSala() { return sala; }
    public Asignatura getAsignatura() { return asignatura; }
    public Horario getHorario() { return horario; }

    // Setter para asociar la GestionSala después de la carga (ej. desde JSON)
    public void setGestionSalaAsociada(GestionSala gestionSala) {
        if (gestionSala != null && !gestionSala.getSala().equals(this.sala)) {
            throw new IllegalArgumentException("La gestión de sala a asociar no corresponde a la sala de esta reserva.");
        }
        this.gestionSalaAsociada = gestionSala;
    }

    // Método para cancelar la reserva, delegando a la GestionSala
    public void cancelar() {
        if (this.gestionSalaAsociada != null) {
            this.gestionSalaAsociada.eliminarHorarioOcupado(this.horario);
        } else {
            System.err.println("Advertencia: No se pudo liberar el horario de la sala porque la GestiónSala asociada no está establecida.");
        }
    }

    @Override
    public String toString() {
        return String.format("Reserva {Profesor: %s, Sala: %s, Asignatura: %s, Horario: %s}",
                profesor.getNombre(),
                sala.getNombre(),
                asignatura.getNombre(),
                horario.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva otra = (Reserva) o;
        return Objects.equals(getSala(), otra.getSala()) &&
                Objects.equals(getHorario(), otra.getHorario());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSala(), getHorario());
    }
}