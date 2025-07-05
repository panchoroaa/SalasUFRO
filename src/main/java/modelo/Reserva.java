package modelo;

import java.util.Objects;

// Asegúrate de que las clases Profesor, Sala, Asignatura y Horario estén en el paquete modelo
// y que Asignatura y Horario tengan sus métodos equals y hashCode implementados.

public class Reserva {
    private final Profesor profesor;     // Quién realiza la reserva
    private final Sala sala;             // La sala que se está reservando
    private final Asignatura asignatura; // La asignatura para la que se reserva la sala
    private final Horario horario;       // El día y bloque horario de la reserva

    // Constructor completo para crear una Reserva válida
    public Reserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        // Validaciones para asegurar que todos los objetos esenciales de la reserva no son nulos
        if (profesor == null) {
            throw new IllegalArgumentException("El profesor no puede ser nulo en una reserva.");
        }
        if (sala == null) {
            throw new IllegalArgumentException("La sala no puede ser nula en una reserva.");
        }
        if (asignatura == null) {
            throw new IllegalArgumentException("La asignatura no puede ser nula en una reserva.");
        }
        if (horario == null) {
            throw new IllegalArgumentException("El horario no puede ser nulo en una reserva.");
        }

        this.profesor = profesor;
        this.sala = sala;
        this.asignatura = asignatura;
        this.horario = horario;
    }

    // --- Getters para acceder a los componentes de la reserva ---
    public Profesor getProfesor() {
        return profesor;
    }

    public Sala getSala() {
        return sala;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public Horario getHorario() {
        return horario;
    }

    @Override
    public String toString() {
        // Representación de texto completa de la reserva para facilidad de lectura
        return String.format("Reserva {Profesor: %s, Sala: %s, Asignatura: %s, Horario: %s}",
                profesor.getNombre(),
                sala.getNombre(),
                asignatura.getNombre(),
                horario.toString());
    }

    // --- Métodos equals() y hashCode() ---
    // Dos reservas se consideran iguales si ocupan la misma sala en el mismo horario.
    // Esto es fundamental para detectar conflictos y gestionar la lista de reservas.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        // Una reserva es única por la combinación de sala y horario.
        // Si más adelante necesitas que la misma sala+horario pueda ser reservada por distintas personas
        // en diferentes "fechas" (ej. "Lunes Bloque 1" cada semana), necesitarías un atributo LocalDate.
        return sala.equals(reserva.sala) && horario.equals(reserva.horario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sala, horario);
    }
}