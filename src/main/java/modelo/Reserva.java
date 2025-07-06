package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    private Profesor profesor;
    private Sala sala;
    private Asignatura asignatura;
    private Horario horario; // Día y bloque horario

    public Reserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        this.profesor = profesor;
        this.sala = sala;
        this.asignatura = asignatura;
        this.horario = horario;
    }

    public Reserva() {}

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "Profesor: " + profesor.getNombre() +
                ", Sala: " + sala.getNombre() +
                ", Asignatura: " + asignatura.getNombre() +
                ", Horario: " + horario.getDia() + " " + horario.getBloque().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(profesor, reserva.profesor) &&
                Objects.equals(sala, reserva.sala) &&
                Objects.equals(horario, reserva.horario) &&
                Objects.equals(asignatura, reserva.asignatura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profesor, sala, asignatura, horario);
    }
}