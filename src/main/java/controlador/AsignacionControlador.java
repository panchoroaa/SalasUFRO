package controlador;

import modelo.*;
import persistencia.JsonDataManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AsignacionControlador {

    private final JsonDataManager dataManager;
    private final List<Profesor> profesores;
    private final List<Sala> salas;
    private final List<Asignatura> asignaturas;
    private final List<Reserva> reservas;

    public AsignacionControlador(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.profesores = dataManager.cargarProfesores();
        this.salas = dataManager.cargarSalas();
        this.asignaturas = dataManager.cargarAsignaturas();
        this.reservas = dataManager.cargarReservas();
    }
    public List<Reserva> filtrarReservas(String rutProfesor, String nombreSala, String codigoAsignatura, DiaSemana dia) {
        return reservas.stream()
                .filter(r -> r != null && r.getHorario() != null && r.getHorario().getDia() != null)
                .filter(r -> (rutProfesor == null || rutProfesor.isEmpty() || r.getRutProfesor().equalsIgnoreCase(rutProfesor)))
                .filter(r -> (nombreSala == null || nombreSala.isEmpty() || r.getNombreSala().equalsIgnoreCase(nombreSala)))
                .filter(r -> (codigoAsignatura == null || codigoAsignatura.isEmpty() || r.getCodigoAsignatura().equalsIgnoreCase(codigoAsignatura))) // Añadir esta línea
                .filter(r -> (dia == null || r.getHorario().getDia() == dia))
                .collect(Collectors.toList());
    }

    public List<Profesor> getProfesores() { return profesores; }
    public List<Sala> getSalas() { return salas; }
    public List<Asignatura> getAsignaturas() { return asignaturas; }
    public List<Reserva> getReservas() { return reservas; }

    public Optional<Profesor> getProfesorPorRut(String rut) {
        return profesores.stream().filter(p -> p != null && p.getRut().equals(rut)).findFirst();
    }

    public Optional<Sala> getSalaPorNombre(String nombre) {
        return salas.stream().filter(s -> s != null && s.getNombre().equals(nombre)).findFirst();
    }

    public Optional<Asignatura> getAsignaturaPorCodigo(String codigo) {
        return asignaturas.stream().filter(a -> a != null && a.getCodigo().equals(codigo)).findFirst();
    }

    public String crearAsignacion(String rutProfesor, String nombreSala, String codigoAsignatura, Horario horario) {
        Optional<Profesor> profesorOpt = getProfesorPorRut(rutProfesor);
        Optional<Sala> salaOpt = getSalaPorNombre(nombreSala);
        Optional<Asignatura> asignaturaOpt = getAsignaturaPorCodigo(codigoAsignatura);

        if (profesorOpt.isEmpty()) {
            return "Error: Profesor con RUT " + rutProfesor + " no encontrado.";
        }
        if (salaOpt.isEmpty()) {
            return "Error: Sala " + nombreSala + " no encontrada.";
        }
        if (asignaturaOpt.isEmpty()) {
            return "Error: Asignatura con código " + codigoAsignatura + " no encontrada.";
        }

        Profesor profesor = profesorOpt.get();
        Sala sala = salaOpt.get();
        Asignatura asignatura = asignaturaOpt.get();

        if (!profesor.imparteAsignatura(asignatura.getCodigo())) {
            return "Error: El profesor " + profesor.getNombre() + " no imparte la asignatura " + asignatura.getNombre() + ".";
        }

        if (asignatura.getCantidadAlumnos() > sala.getCapacidad()) {
            return "Error: La cantidad de alumnos de la asignatura (" + asignatura.getCantidadAlumnos() +
                    ") excede la capacidad de la sala (" + sala.getCapacidad() + ").";
        }

        if (!sala.estaDisponibleEn(horario)) {
            return "Error: La sala " + sala.getNombre() + " no está disponible en el horario " + horario.toString() +
                    " o su estado no permite asignaciones.";
        }

        if (profesorTieneConflicto(profesor, horario)) {
            return "Error: El profesor " + profesor.getNombre() + " ya tiene una asignación en ese horario.";
        }

        Reserva nuevaReserva = new Reserva(profesor.getRut(), sala.getNombre(), asignatura.getCodigo(), horario);
        reservas.add(nuevaReserva);
        sala.agregarHorarioOcupado(horario);

        dataManager.guardarReservas(reservas);
        dataManager.guardarSalas(salas);

        return "¡Asignación realizada con éxito!";
    }

    public String cancelarAsignacion(Reserva reserva) {
        getSalaPorNombre(reserva.getNombreSala()).ifPresent(sala -> sala.removerHorarioOcupado(reserva.getHorario()));

        boolean removido = reservas.remove(reserva);

        if (!removido) {
            return "Error: La asignación no fue encontrada para cancelar.";
        }

        dataManager.guardarReservas(reservas);
        dataManager.guardarSalas(salas);
        return "¡Asignación cancelada con éxito!";
    }

    private boolean profesorTieneConflicto(Profesor profesor, Horario horario) {
        return reservas.stream()
                .filter(r -> r != null && r.getRutProfesor() != null && r.getHorario() != null)
                .filter(r -> r.getRutProfesor().equals(profesor.getRut()))
                .anyMatch(r -> r.getHorario().equals(horario));
    }

    public List<Profesor> getProfesoresDisponibles(Asignatura asignatura, Horario horario) {
        return profesores.stream()
                .filter(p -> p != null && p.imparteAsignatura(asignatura.getCodigo()))
                .filter(p -> !profesorTieneConflicto(p, horario))
                .collect(Collectors.toList());
    }

    public List<Sala> getSalasDisponiblesEnHorario(Horario horario) {
        return salas.stream()
                .filter(s -> s != null && s.estaDisponibleEn(horario))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorProfesor(String rutProfesor) {
        return reservas.stream()
                .filter(r -> r != null && r.getRutProfesor() != null)
                .filter(r -> r.getRutProfesor().equals(rutProfesor))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorSala(String nombreSala) {
        return reservas.stream()
                .filter(r -> r != null && r.getNombreSala() != null)
                .filter(r -> r.getNombreSala().equals(nombreSala))
                .collect(Collectors.toList());
    }

    public List<Profesor> buscarProfesores(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();
        return profesores.stream()
                .filter(p -> p != null && (p.getNombre().toLowerCase().contains(lowerCaseQuery) ||
                        p.getRut().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    public List<Sala> buscarSalas(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();
        return salas.stream()
                .filter(s -> s != null && s.getNombre().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    public List<Asignatura> buscarAsignaturas(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();
        return asignaturas.stream()
                .filter(a -> a != null && (a.getNombre().toLowerCase().contains(lowerCaseQuery) ||
                        a.getCodigo().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    public List<Reserva> filtrarReservas(String rutProfesor, String nombreSala, DiaSemana dia) {
        return reservas.stream()
                .filter(r -> r != null && r.getHorario() != null && r.getHorario().getDia() != null)
                .filter(r -> (rutProfesor == null || rutProfesor.isEmpty() || r.getRutProfesor().equalsIgnoreCase(rutProfesor)))
                .filter(r -> (nombreSala == null || nombreSala.isEmpty() || r.getNombreSala().equalsIgnoreCase(nombreSala)))
                .filter(r -> (dia == null || r.getHorario().getDia() == dia))
                .collect(Collectors.toList());
    }
}