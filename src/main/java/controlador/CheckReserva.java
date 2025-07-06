// controlador/CheckReserva.java
package controlador;

import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.Profesor;
import modelo.Sala;
import modelo.GestionSala;
import modelo.Horario;
import modelo.Reserva;
import modelo.RutNotFoundException;
import persistencia.JsonDataManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckReserva {

    private final JsonDataManager jsonDataManager;

    public CheckReserva() {
        this.jsonDataManager = new JsonDataManager();
    }

    public String obtenerIdProfesor(String rut) throws RutNotFoundException {
        List<Profesor> profesores = jsonDataManager.cargarProfesores();
        return profesores.stream()
                .filter(p -> p.getRut().equalsIgnoreCase(rut))
                .map(Profesor::getId)
                .findFirst()
                .orElseThrow(() -> new RutNotFoundException("El RUT " + rut + " no se encuentra en la base de datos de profesores."));
    }

    public boolean existeProfesor(String rut) {
        try {
            return obtenerIdProfesor(rut) != null;
        } catch (RutNotFoundException e) {
            return false;
        }
    }

    public Profesor obtenerProfesorPorRut(String rut) {
        List<Profesor> profesores = jsonDataManager.cargarProfesores();
        return profesores.stream()
                .filter(p -> p.getRut().equalsIgnoreCase(rut))
                .findFirst()
                .orElse(null);
    }

    public boolean existeSala(String nombreSala) {
        List<GestionSala> gestionesSalas = jsonDataManager.cargarGestionesSalas();
        return gestionesSalas.stream()
                .anyMatch(gs -> gs.getSala().getNombre().equalsIgnoreCase(nombreSala));
    }

    public Sala obtenerSalaPorNombre(String nombreSala) {
        List<GestionSala> gestionesSalas = jsonDataManager.cargarGestionesSalas();
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().getNombre().equalsIgnoreCase(nombreSala))
                .map(GestionSala::getSala)
                .findFirst()
                .orElse(null);
    }

    public GestionSala obtenerGestionSalaPara(Sala salaInformativa) {
        List<GestionSala> gestionesSalas = jsonDataManager.cargarGestionesSalas();
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().equals(salaInformativa))
                .findFirst()
                .orElse(null);
    }

    public boolean profesorImparteAsignatura(Profesor profesor, String codigoRamo) {
        if (profesor == null) {
            return false;
        }
        return profesor.tieneAsignatura(codigoRamo);
    }

    public boolean salaEstaDisponible(Sala salaInformativa, String dia, BloqueHorario bloque) {
        GestionSala gestionSala = obtenerGestionSalaPara(salaInformativa);
        if (gestionSala == null) {
            return false;
        }
        Horario horario = new Horario(dia, bloque);
        return gestionSala.estaDisponible(horario);
    }

    public boolean profesorTieneConflictoHorario(Profesor profesor, String dia, BloqueHorario bloque) {
        if (profesor == null) {
            return false;
        }
        List<Profesor> todosProfesores = jsonDataManager.cargarProfesores();
        List<GestionSala> todasGestionesSalas = jsonDataManager.cargarGestionesSalas();
        List<Reserva> reservas = jsonDataManager.cargarReservas(todosProfesores, todasGestionesSalas);

        Horario horarioPropuesto = new Horario(dia, bloque);

        return reservas.stream()
                .filter(r -> r.getProfesor().equals(profesor))
                .anyMatch(r -> r.getHorario().conflictuaCon(horarioPropuesto));
    }

    public boolean profesorTieneReservasActivas(Profesor profesor) {
        if (profesor == null) {
            return false;
        }
        List<Profesor> todosProfesores = jsonDataManager.cargarProfesores();
        List<GestionSala> todasGestionesSalas = jsonDataManager.cargarGestionesSalas();
        List<Reserva> reservas = jsonDataManager.cargarReservas(todosProfesores, todasGestionesSalas);

        return reservas.stream()
                .anyMatch(r -> r.getProfesor().equals(profesor));
    }

    public boolean salaTieneReservasActivas(Sala sala) {
        if (sala == null) {
            return false;
        }
        List<Profesor> todosProfesores = jsonDataManager.cargarProfesores();
        List<GestionSala> todasGestionesSalas = jsonDataManager.cargarGestionesSalas();
        List<Reserva> reservas = jsonDataManager.cargarReservas(todosProfesores, todasGestionesSalas);

        return reservas.stream()
                .anyMatch(r -> r.getSala().equals(sala));
    }
}