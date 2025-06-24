package modelo;

public class Asignacion {
    private Profesor profesor;
    private Sala sala;
    private Horario horario;

    public Asignacion(Profesor profesor, Sala sala, Horario horario) {
        this.profesor = profesor;
        this.sala = sala;
        this.horario = horario;
    }

    public Profesor getProfesor() { return profesor; }
    public Sala getSala() { return sala; }
    public Horario getHorario() { return horario; }
}