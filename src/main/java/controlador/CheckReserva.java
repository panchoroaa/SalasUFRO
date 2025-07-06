package controlador;

import modelo.BloqueHorario;
import modelo.Profesor;
import modelo.Sala;
import modelo.Horario;
import modelo.GestionSala;

import java.util.List;
import java.util.stream.Collectors;

public class CheckReserva {
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;

    public CheckReserva(ProfesorControlador profesorControlador,
                        SalaControlador salaControlador,
                        AsignacionControlador asignacionControlador) {
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.asignacionControlador = asignacionControlador;
    }

    public boolean salaEstaDisponible(Sala sala, String dia, BloqueHorario bloque) {
        if (sala == null || dia == null || bloque == null) {
            return false;
        }

        GestionSala gestionSala = salaControlador.getGestionSalaPara(sala);
        if (gestionSala == null) {
            System.err.println("Error interno: No se encontró gestión para la sala " + sala.getNombre());
            return false;
        }

        Horario nuevoHorario = new Horario(dia, bloque);
        return gestionSala.estaDisponible(nuevoHorario);
    }

    public boolean profesorTieneConflictoHorario(Profesor profesor, String dia, BloqueHorario bloque) {
        if (profesor == null || dia == null || bloque == null) {
            return false;
        }

        Horario horarioConflicto = new Horario(dia, bloque);

        return asignacionControlador.getReservas().stream()
                .anyMatch(reserva -> reserva.getProfesor() != null && reserva.getProfesor().equals(profesor) &&
                        reserva.getHorario() != null && reserva.getHorario().equals(horarioConflicto));
    }
}