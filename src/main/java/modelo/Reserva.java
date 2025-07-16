package modelo;

import java.util.Objects;

public class Reserva {
    private String rutProfesor;
    private String nombreSala;
    private String codigoAsignatura;
    private Horario horario; // This must be of type Horario

    public Reserva(String rutProfesor, String nombreSala, String codigoAsignatura, Horario horario) {
        this.rutProfesor = rutProfesor;
        this.nombreSala = nombreSala;
        this.codigoAsignatura = codigoAsignatura;
        this.horario = horario;
    }

    public Reserva() {} // Constructor sin argumentos necesario para Jackson

    // --- Getters y Setters ---
    public String getRutProfesor() { return rutProfesor; }
    public void setRutProfesor(String rutProfesor) { this.rutProfesor = rutProfesor; }

    public String getNombreSala() { return nombreSala; }
    public void setNombreSala(String nombreSala) { this.nombreSala = nombreSala; }

    public String getCodigoAsignatura() { return codigoAsignatura; }
    public void setCodigoAsignatura(String codigoAsignatura) { this.codigoAsignatura = codigoAsignatura; }

    // Este getter es crucial y debe devolver un objeto Horario
    public Horario getHorario() { return horario; }
    public void setHorario(Horario horario) { this.horario = horario; }

    // Método para una representación legible (requiere acceso al controlador o servicio para obtener los objetos completos)
    public String toStringCompleto(Profesor p, Sala s, Asignatura a) {
        return String.format("Sala: %-12s | Horario: %-25s | Profesor: %-25s | Asignatura: %s",
                s.getNombre(),
                horario.toString(), // Llama al toString de Horario
                p.getNombre(),
                a.getNombre()
        );
    }

    @Override
    public String toString() {
        return String.format("Reserva: Prof: %s | Sala: %s | Asig: %s | Horario: %s",
                rutProfesor, nombreSala, codigoAsignatura, horario != null ? horario.toString() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(rutProfesor, reserva.rutProfesor) &&
                Objects.equals(nombreSala, reserva.nombreSala) &&
                Objects.equals(codigoAsignatura, reserva.codigoAsignatura) &&
                Objects.equals(horario, reserva.horario); // Compara objetos Horario
    }

    @Override
    public int hashCode() {
        return Objects.hash(rutProfesor, nombreSala, codigoAsignatura, horario);
    }
}