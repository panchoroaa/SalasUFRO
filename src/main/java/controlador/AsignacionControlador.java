package controlador;

import modelo.*;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AsignacionControlador {
    private final Scanner scanner;
    private final SalaControlador salaControlador;
    private final ProfesorControlador profesorControlador;
    private final JsonDataManager jsonDataManager;
    private List<Reserva> reservas;

    public AsignacionControlador() {
        this.scanner = new Scanner(System.in);
        this.salaControlador = new SalaControlador();
        this.profesorControlador = new ProfesorControlador();
        this.jsonDataManager = new JsonDataManager();
        this.reservas = jsonDataManager.cargarReservas();
    }

    public void asignarSalaAProfesor() {
        System.out.println("\n=== Asignación de Sala a Profesor ===");

        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        List<Sala> salas = salaControlador.getSalasRegistradas();

        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados. Registre profesores primero.");
            return;
        }

        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas. Registre salas primero.");
            return;
        }

        // Selección del profesor
        Profesor profesorSeleccionado = seleccionarProfesor(profesores);
        if (profesorSeleccionado == null) return;

        // Selección de la asignatura
        Asignatura asignaturaSeleccionada = seleccionarAsignatura(profesorSeleccionado);
        if (asignaturaSeleccionada == null) return;

        // Selección del horario
        Horario horarioSeleccionado = seleccionarHorario();
        if (horarioSeleccionado == null) return;

        // Verificar si el profesor ya tiene una reserva en ese horario
        if (profesorTieneReserva(profesorSeleccionado, horarioSeleccionado)) {
            System.out.println("El profesor ya tiene una asignación en este horario.");
            return;
        }

        // Filtrar salas disponibles según capacidad y horario
        List<Sala> salasDisponibles = filtrarSalasDisponibles(salas, asignaturaSeleccionada.getCantidadAlumnos(), horarioSeleccionado);

        if (salasDisponibles.isEmpty()) {
            System.out.println("No hay salas disponibles que cumplan con los requisitos.");
            return;
        }

        // Selección de la sala
        Sala salaSeleccionada = seleccionarSala(salasDisponibles);
        if (salaSeleccionada == null) return;

        // Crear y guardar la reserva
        realizarReserva(profesorSeleccionado, salaSeleccionada, asignaturaSeleccionada, horarioSeleccionado);
    }

    private Profesor seleccionarProfesor(List<Profesor> profesores) {
        System.out.println("\nProfesores disponibles:");
        for (int i = 0; i < profesores.size(); i++) {
            Profesor prof = profesores.get(i);
            System.out.printf("%d. %s (RUT: %s)%n", i + 1, prof.getNombre(), prof.getRut());
        }

        while (true) {
            System.out.print("\nSeleccione el número del profesor (0 para cancelar): ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion == 0) return null;
                if (opcion > 0 && opcion <= profesores.size()) {
                    return profesores.get(opcion - 1);
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private Asignatura seleccionarAsignatura(Profesor profesor) {
        List<Asignatura> asignaturas = profesor.getAsignaturasImpartidas();
        if (asignaturas.isEmpty()) {
            System.out.println("El profesor no tiene asignaturas registradas.");
            return null;
        }

        System.out.println("\nAsignaturas del profesor:");
        for (int i = 0; i < asignaturas.size(); i++) {
            Asignatura asig = asignaturas.get(i);
            System.out.printf("%d. %s (%s) - %d alumnos%n",
                    i + 1, asig.getNombre(), asig.getCodigo(), asig.getCantidadAlumnos());
        }

        while (true) {
            System.out.print("\nSeleccione el número de la asignatura (0 para cancelar): ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion == 0) return null;
                if (opcion > 0 && opcion <= asignaturas.size()) {
                    return asignaturas.get(opcion - 1);
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private Horario seleccionarHorario() {
        System.out.println("\nSelección de horario");
        System.out.print("Ingrese el día (Lunes-Viernes): ");
        String dia = scanner.nextLine().trim();

        if (!validarDia(dia)) {
            System.out.println("Día inválido. Debe ser un día de la semana (Lunes-Viernes).");
            return null;
        }

        System.out.println("\nBloques disponibles:");
        for (BloqueHorario bloque : BloqueHorario.values()) {
            System.out.println(bloque.ordinal() + 1 + ". " + bloque);
        }

        while (true) {
            System.out.print("\nSeleccione el número del bloque (0 para cancelar): ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion == 0) return null;
                if (opcion > 0 && opcion <= BloqueHorario.values().length) {
                    return new Horario(dia, BloqueHorario.values()[opcion - 1]);
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private boolean validarDia(String dia) {
        String diaLower = dia.toLowerCase();
        return diaLower.equals("lunes") || diaLower.equals("martes") ||
                diaLower.equals("miercoles") || diaLower.equals("jueves") ||
                diaLower.equals("viernes");
    }

    private List<Sala> filtrarSalasDisponibles(List<Sala> salas, int capacidadRequerida, Horario horario) {
        List<Sala> salasDisponibles = new ArrayList<>();
        for (Sala sala : salas) {
            if (sala.getCapacidad() >= capacidadRequerida &&
                    sala.getEstado().equalsIgnoreCase("Disponible") &&
                    sala.estaDisponible(horario)) {
                salasDisponibles.add(sala);
            }
        }
        return salasDisponibles;
    }

    private Sala seleccionarSala(List<Sala> salasDisponibles) {
        System.out.println("\nSalas disponibles:");
        for (int i = 0; i < salasDisponibles.size(); i++) {
            Sala sala = salasDisponibles.get(i);
            System.out.printf("%d. %s (Capacidad: %d)%n",
                    i + 1, sala.getNombre(), sala.getCapacidad());
        }

        while (true) {
            System.out.print("\nSeleccione el número de la sala (0 para cancelar): ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion == 0) return null;
                if (opcion > 0 && opcion <= salasDisponibles.size()) {
                    return salasDisponibles.get(opcion - 1);
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private boolean profesorTieneReserva(Profesor profesor, Horario horario) {
        for (Reserva reserva : reservas) {
            if (reserva.getProfesor().equals(profesor) &&
                    reserva.getHorario().equals(horario)) {
                return true;
            }
        }
        return false;
    }

    private void realizarReserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        Reserva nuevaReserva = new Reserva(profesor, sala, asignatura, horario);
        reservas.add(nuevaReserva);
        sala.agregarHorarioOcupado(horario);

        jsonDataManager.guardarReservas(reservas);
        salaControlador.guardarTodasLasSalasEnArchivo();

        System.out.println("\n¡Reserva realizada exitosamente!");
        System.out.println(nuevaReserva);
    }

    public void verAsignaciones() {
        if (reservas.isEmpty()) {
            System.out.println("\nNo hay asignaciones registradas.");
            return;
        }

        System.out.println("\n=== Asignaciones Actuales ===");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva reserva = reservas.get(i);
            System.out.printf("%d. Sala: %s | Profesor: %s | Asignatura: %s | %s, %s%n",
                    i + 1,
                    reserva.getSala().getNombre(),
                    reserva.getProfesor().getNombre(),
                    reserva.getAsignatura().getNombre(),
                    reserva.getHorario().getDia(),
                    reserva.getHorario().getBloque());
        }
    }

    public void cancelarAsignacion() {
        if (reservas.isEmpty()) {
            System.out.println("\nNo hay asignaciones para cancelar.");
            return;
        }

        verAsignaciones();

        while (true) {
            System.out.print("\nSeleccione el número de la asignación a cancelar (0 para cancelar): ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                if (opcion == 0) return;

                if (opcion > 0 && opcion <= reservas.size()) {
                    Reserva reservaACancelar = reservas.remove(opcion - 1);
                    reservaACancelar.getSala().eliminarHorarioOcupado(reservaACancelar.getHorario());

                    jsonDataManager.guardarReservas(reservas);
                    salaControlador.guardarTodasLasSalasEnArchivo();

                    System.out.println("\nAsignación cancelada exitosamente.");
                    return;
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }
}