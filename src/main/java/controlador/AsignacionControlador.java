package controlador;

import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.GestionSala;
import modelo.Horario;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AsignacionControlador {
    private final JsonDataManager jsonDataManager;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private List<Reserva> reservas;
    private final CheckReserva checkReserva;

    public AsignacionControlador(ProfesorControlador profesorControlador,
                                 SalaControlador salaControlador,
                                 JsonDataManager jsonDataManager) {
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.jsonDataManager = jsonDataManager;
        // Importante: Cargar reservas requiere que profesores y salas ya estén cargados
        // Se carga aquí y se pasa las listas de referencia, o se carga en MenuSalas y se pasa ya cargado
        this.reservas = new ArrayList<>(); // Inicializar vacío por si la carga falla
        this.checkReserva = new CheckReserva(profesorControlador, salaControlador, this);

        // Cargar reservas después de que los controladores y checkReserva estén listos
        // Esto se hará en MenuSalas, donde se inician todos los controladores
        // Para evitar un ciclo de dependencia en el constructor, la carga se hace en MenuSalas
        // y se pasa a setReservas()
    }

    // Método para cargar reservas desde MenuSalas una vez que todo está inicializado
    public void cargarReservasDesdeDataManager() {
        // Necesitamos listas de profesores y salas para cargar reservas
        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        List<Sala> salas = salaControlador.getSalasRegistradasPuras(); // Usar un método que devuelva solo Sala
        List<Asignatura> asignaturas = jsonDataManager.cargarAsignaturas(); // Cargar asignaturas

        this.reservas = jsonDataManager.cargarReservas(profesores, salas, asignaturas);
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public String realizarAsignacion(Profesor profesor, Sala sala, Asignatura asignatura, String dia, BloqueHorario bloque) {
        if (profesor == null || sala == null || asignatura == null || dia == null || bloque == null) {
            return "Error: Faltan datos para realizar la asignación.";
        }

        if (!checkReserva.salaEstaDisponible(sala, dia, bloque)) {
            return "Error: La sala " + sala.getNombre() + " no está disponible en el horario " + dia + " " + bloque.toString() + " o está en mantenimiento.";
        }
        if (checkReserva.profesorTieneConflictoHorario(profesor, dia, bloque)) {
            return "Error: El profesor " + profesor.getNombre() + " ya tiene una asignación en el horario " + dia + " " + bloque.toString() + ".";
        }

        if (sala.getCapacidad() < asignatura.getCantidadAlumnos()) {
            return "Error: La sala " + sala.getNombre() + " (capacidad: " + sala.getCapacidad() + ") no tiene capacidad suficiente para " + asignatura.getCantidadAlumnos() + " alumnos de " + asignatura.getNombre() + ".";
        }
        if (!profesor.tieneAsignatura(asignatura.getCodigo())) {
            return "Error: El profesor " + profesor.getNombre() + " no imparte la asignatura " + asignatura.getNombre() + ".";
        }

        GestionSala gestionSala = salaControlador.getGestionSalaPara(sala);
        if (gestionSala == null) {
            return "Error interno: No se pudo obtener la gestión de sala para " + sala.getNombre() + ".";
        }

        Horario nuevoHorario = new Horario(dia, bloque);
        try {
            gestionSala.agregarHorarioOcupado(nuevoHorario);
            // El guardado de GestionSala se hace automáticamente al guardar cambios en SalaControlador
            salaControlador.guardarCambiosEnGestionSala(gestionSala); // Persiste la gestionSala modificada
        } catch (IllegalStateException e) {
            return "Error al actualizar la disponibilidad de la sala: " + e.getMessage();
        }

        Reserva nuevaReserva = new Reserva(profesor, sala, asignatura, nuevoHorario);
        reservas.add(nuevaReserva);
        jsonDataManager.guardarReservas(reservas);

        return "Asignación realizada exitosamente:\n" + nuevaReserva.toString();
    }

    public String cancelarAsignacion(int index) {
        if (index < 0 || index >= reservas.size()) {
            return "Error: Índice de asignación no válido.";
        }
        Reserva reservaACancelar = reservas.get(index);

        GestionSala gestionSala = salaControlador.getGestionSalaPara(reservaACancelar.getSala());
        if (gestionSala != null) {
            try {
                gestionSala.removerHorarioOcupado(reservaACancelar.getHorario());
                salaControlador.guardarCambiosEnGestionSala(gestionSala); // Persiste la gestionSala modificada
            } catch (IllegalArgumentException e) {
                System.err.println("Advertencia: El horario no se pudo remover de la gestión de sala. Posible inconsistencia de datos.");
            }
        }

        reservas.remove(index);
        jsonDataManager.guardarReservas(reservas);
        return "Asignación cancelada exitosamente:\n" + reservaACancelar.toString();
    }

    public void listarAsignaciones() {
        if (reservas.isEmpty()) {
            System.out.println("No hay asignaciones registradas.");
            return;
        }
        System.out.println("\n--- Lista de Asignaciones ---");
        // Asegúrate de que los objetos dentro de Reserva no sean nulos al imprimirlos
        reservas.forEach(System.out::println);
    }

    public List<String> getReservasParaUI() {
        return reservas.stream()
                .map(Reserva::toString)
                .collect(Collectors.toList());
    }

    public boolean profesorTieneReservasActivas(Profesor profesor) {
        if (profesor == null) return false;
        return reservas.stream()
                .anyMatch(r -> r.getProfesor() != null && r.getProfesor().equals(profesor));
    }

    public boolean salaTieneReservasActivas(Sala sala) {
        if (sala == null) return false;
        return reservas.stream()
                .anyMatch(r -> r.getSala() != null && r.getSala().equals(sala));
    }
}