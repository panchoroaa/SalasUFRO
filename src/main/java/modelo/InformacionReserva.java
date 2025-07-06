package modelo;

public class InformacionReserva {
    private final Profesor profesor;
    private final Sala sala;
    private final Asignatura asignatura;
    private final Horario horario;

    public InformacionReserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        if (profesor == null || sala == null || asignatura == null || horario == null) {
            throw new IllegalArgumentException("Ningún parámetro puede ser nulo");
        }

        this.profesor = profesor;
        this.sala = sala;
        this.asignatura = asignatura;
        this.horario = horario;
    }

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
        return String.format("Reserva {Profesor: %s, Sala: %s, Asignatura: %s, Horario: %s}",
                profesor.getNombre(),
                sala.getNombre(),
                asignatura.getNombre(),
                horario.toString());
    }
}