package controlador;

import modelo.*;
import persistencia.JsonDataManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador principal que centraliza toda la lógica de negocio.
 * Actúa como intermediario entre la Vista y el Modelo/Persistencia.
 */
public class AsignacionControlador {

    private final JsonDataManager dataManager;
    private final List<Profesor> profesores;
    private final List<Sala> salas;
    private final List<Asignatura> asignaturas;
    private final List<Reserva> reservas;

    public AsignacionControlador(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        // Carga todos los datos al momento de la creación.
        this.profesores = dataManager.cargarProfesores();
        this.salas = dataManager.cargarSalas();
        this.asignaturas = dataManager.cargarAsignaturas();
        this.reservas = dataManager.cargarReservas();
    }

    // --- Métodos para la VISTA ---
    public List<Profesor> getProfesores() { return profesores; }
    public List<Sala> getSalas() { return salas; }
    public List<Asignatura> getAsignaturas() { return asignaturas; }
    public List<Reserva> getReservas() { return reservas; }

    public Optional<Profesor> getProfesorPorRut(String rut) {
        return profesores.stream()
                .filter(p -> p.getRut().equals(rut))
                .findFirst();
    }

    public Optional<Sala> getSalaPorNombre(String nombre) {
        return salas.stream()
                .filter(s -> s.getNombre().equals(nombre))
                .findFirst();
    }

    public Optional<Asignatura> getAsignaturaPorCodigo(String codigo) {
        return asignaturas.stream()
                .filter(a -> a.getCodigo().equals(codigo))
                .findFirst();
    }

    public List<Reserva> getReservasPorSala(String nombreSala) {
        return reservas.stream()
                .filter(r -> r.getNombreSala().equals(nombreSala))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorProfesor(String rutProfesor) {
        return reservas.stream()
                .filter(r -> r.getRutProfesor().equals(rutProfesor))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista de profesores que imparten una asignatura dada
     * y están disponibles en un horario específico.
     */
    public List<Profesor> getProfesoresDisponibles(Asignatura asignatura, Horario horario) {
        return profesores.stream()
                .filter(p -> p.imparteAsignatura(asignatura.getCodigo())) // Imparte la asignatura
                .filter(p -> !profesorTieneConflicto(p, horario))         // Está disponible en ese horario
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista de salas que están DISPONIBLES y libres en un horario específico.
     */
    public List<Sala> getSalasDisponiblesEnHorario(Horario horario) {
        return salas.stream()
                .filter(s -> s.getEstado() == EstadoSala.DISPONIBLE) // Estado de la sala es DISPONIBLE
                .filter(s -> s.estaDisponibleEn(horario))             // Está libre en ese horario
                .collect(Collectors.toList());
    }

    /**
     * Realiza una nueva asignación de sala.
     */
    public String realizarAsignacion(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        // Validaciones previas (aunque muchas se manejan ahora al seleccionar elementos)
        if (!sala.estaDisponibleEn(horario)) {
            return "Error: La sala " + sala.getNombre() + " no está disponible en ese horario o su estado no es DISPONIBLE.";
        }
        if (profesorTieneConflicto(profesor, horario)) {
            return "Error: El profesor " + profesor.getNombre() + " ya tiene una asignación en ese horario.";
        }

        // Si todas las validaciones pasan, se crea la reserva
        Reserva nuevaReserva = new Reserva(profesor.getRut(), sala.getNombre(), asignatura.getCodigo(), horario);
        reservas.add(nuevaReserva);
        sala.agregarHorarioOcupado(horario);

        // Guardar cambios en la persistencia
        dataManager.guardarReservas(reservas);
        dataManager.guardarSalas(salas);

        return "¡Asignación realizada con éxito!";
    }

    /**
     * Cancela una reserva existente.
     */
    public String cancelarAsignacion(Reserva reserva) {
        // Liberar el horario en la sala correspondiente
        getSalaPorNombre(reserva.getNombreSala()).ifPresent(sala -> {
            sala.removerHorarioOcupado(reserva.getHorario());
        });

        // Eliminar la reserva de la lista
        reservas.remove(reserva);

        // Guardar cambios
        dataManager.guardarReservas(reservas);
        dataManager.guardarSalas(salas);

        return "¡Asignación cancelada con éxito!";
    }

    // --- Métodos de Ayuda Privados ---
    private boolean profesorTieneConflicto(Profesor profesor, Horario horario) {
        return reservas.stream()
                .filter(r -> r.getRutProfesor().equals(profesor.getRut()))
                .anyMatch(r -> r.getHorario().equals(horario));
    }
}