package controlador;

import modelo.*;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import controlador.SelectorBloqueHorario;

public class AsignacionControlador {
    private final Scanner scanner;
    private final SalaControlador salaControlador; // Se necesita para obtener salas y guardar cambios
    private final ProfesorControlador profesorControlador; // Se necesita para obtener profesores
    private final JsonDataManager jsonDataManager;
    private List<Reserva> reservas;

    // Se ajusta el constructor para que las listas de profesores y salas se pasen
    // Esto es CRUCIAL para que cargarReservas pueda reconstruir las referencias a objetos
    public AsignacionControlador(ProfesorControlador profesorControlador, SalaControlador salaControlador) {
        this.scanner = new Scanner(System.in);
        this.profesorControlador = profesorControlador; // Recibe la instancia existente
        this.salaControlador = salaControlador;       // Recibe la instancia existente
        this.jsonDataManager = new JsonDataManager();
        // Cargar las reservas pasando las listas actualizadas de profesores y salas
        this.reservas = jsonDataManager.cargarReservas(this.profesorControlador.getProfesoresRegistrados(), this.salaControlador.getSalasRegistradas());
    }

    public void asignarSalaAProfesor() {
        System.out.println("\n=== Asignación de Sala a Profesor ===");
        System.out.println("Ingrese '0' en cualquier momento para cancelar y volver al menú principal.");

        List<Profesor> profesoresDisponibles = profesorControlador.getProfesoresRegistrados();
        List<Sala> salasDisponiblesGlobal = salaControlador.getSalasRegistradas();

        if (profesoresDisponibles.isEmpty()) {
            System.out.println("No hay profesores registrados. Registre profesores primero.");
            return;
        }
        if (salasDisponiblesGlobal.isEmpty()) {
            System.out.println("No hay salas registradas. Registre salas primero.");
            return;
        }

        // 1. Selección del profesor
        Profesor profesorSeleccionado = seleccionarProfesor(profesoresDisponibles);
        if (profesorSeleccionado == null) {
            System.out.println("Asignación de sala cancelada.");
            return;
        }

        // 2. Selección de la asignatura
        Asignatura asignaturaSeleccionada = seleccionarAsignatura(profesorSeleccionado);
        if (asignaturaSeleccionada == null) {
            System.out.println("Asignación de sala cancelada.");
            return;
        }

        // 3. Selección del horario
        Horario horarioSeleccionado = seleccionarHorario();
        if (horarioSeleccionado == null) {
            System.out.println("Asignación de sala cancelada.");
            return;
        }

        // 4. Verificar si el profesor ya tiene una reserva en ese horario
        if (profesorTieneReserva(profesorSeleccionado, horarioSeleccionado)) {
            System.out.println("El profesor ya tiene una asignación en este horario. Asignación cancelada.");
            return;
        }

        // 5. Filtrar salas disponibles según capacidad y horario
        List<Sala> salasCandidatas = filtrarSalasDisponibles(salasDisponiblesGlobal, asignaturaSeleccionada.getCantidadAlumnos(), horarioSeleccionado);

        if (salasCandidatas.isEmpty()) {
            System.out.println("No hay salas disponibles que cumplan con los requisitos (capacidad y horario libre). Asignación cancelada.");
            return;
        }

        // 6. Selección de la sala
        Sala salaSeleccionada = seleccionarSala(salasCandidatas);
        if (salaSeleccionada == null) {
            System.out.println("Asignación de sala cancelada.");
            return;
        }

        // 7. Crear y guardar la reserva
        realizarReserva(profesorSeleccionado, salaSeleccionada, asignaturaSeleccionada, horarioSeleccionado);
    }

    // --- Métodos auxiliares para obtener entrada con opción de cancelar ---
    private String obtenerStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equals("0")) {
            return null; // Indica cancelación
        }
        return input;
    }

    private Profesor seleccionarProfesor(List<Profesor> profesores) {
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores disponibles.");
            return null;
        }
        System.out.println("\nProfesores disponibles:");
        for (int i = 0; i < profesores.size(); i++) {
            Profesor prof = profesores.get(i);
            System.out.printf("%d. %s (RUT: %s)%n", i + 1, prof.getNombre(), prof.getRut());
        }

        while (true) {
            System.out.print("\nSeleccione el número del profesor (0 para cancelar): ");
            String inputOpcion = scanner.nextLine().trim();
            if (inputOpcion.equals("0")) return null;
            try {
                int opcion = Integer.parseInt(inputOpcion);
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

        System.out.println("\nAsignaturas del profesor " + profesor.getNombre() + ":");
        for (int i = 0; i < asignaturas.size(); i++) {
            Asignatura asig = asignaturas.get(i);
            System.out.printf("%d. %s (%s) - %d alumnos%n",
                    i + 1, asig.getNombre(), asig.getCodigo(), asig.getCantidadAlumnos());
        }

        while (true) {
            System.out.print("\nSeleccione el número de la asignatura (0 para cancelar): ");
            String inputOpcion = scanner.nextLine().trim();
            if (inputOpcion.equals("0")) return null;
            try {
                int opcion = Integer.parseInt(inputOpcion);
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
        System.out.println("\nSelección de Horario (0 para cancelar)");

        String dia = obtenerStringInput("Ingrese el día (ej. Lunes, Martes): ");
        if (dia == null) return null; // Cancelado
        if (!validarDia(dia)) {
            System.out.println("Día inválido. Debe ser un día de la semana (Lunes-Viernes). Asignación cancelada.");
            return null;
        }

        // Reutilizamos el SelectorBloqueHorario con opción de cancelar
        BloqueHorario bloque = SelectorBloqueHorario.seleccionarBloqueConOpcionCancelar();
        if (bloque == null) return null; // Cancelado

        return new Horario(dia, bloque);
    }

    private boolean validarDia(String dia) {
        String diaLower = dia.toLowerCase();
        return diaLower.equals("lunes") || diaLower.equals("martes") ||
                diaLower.equals("miercoles") || diaLower.equals("jueves") ||
                diaLower.equals("viernes");
    }

    private List<Sala> filtrarSalasDisponibles(List<Sala> salas, int capacidadRequerida, Horario horario) {
        List<Sala> salasDisponibles = new ArrayList<>();
        System.out.println("\n--- Buscando salas disponibles ---");
        for (Sala sala : salas) {
            boolean cumpleCapacidad = sala.getCapacidad() >= capacidadRequerida;
            boolean salaEsDisponible = sala.getEstado().equalsIgnoreCase("Disponible");
            boolean horarioLibreEnSala = sala.estaDisponible(horario); // Usa el método estaDisponible

            if (cumpleCapacidad && salaEsDisponible && horarioLibreEnSala) {
                salasDisponibles.add(sala);
            } else {
                // Para depuración o información al usuario
                // System.out.printf("Sala '%s' no disponible: Capacidad: %b, Estado: %b, Horario: %b%n",
                //    sala.getNombre(), cumpleCapacidad, salaEsDisponible, horarioLibreEnSala);
            }
        }
        return salasDisponibles;
    }

    private Sala seleccionarSala(List<Sala> salasDisponibles) {
        System.out.println("\nSalas disponibles para asignación:");
        for (int i = 0; i < salasDisponibles.size(); i++) {
            Sala sala = salasDisponibles.get(i);
            System.out.printf("%d. %s (Capacidad: %d, Estado: %s)%n",
                    i + 1, sala.getNombre(), sala.getCapacidad(), sala.getEstado());
        }

        while (true) {
            System.out.print("\nSeleccione el número de la sala (0 para cancelar): ");
            String inputOpcion = scanner.nextLine().trim();
            if (inputOpcion.equals("0")) return null;
            try {
                int opcion = Integer.parseInt(inputOpcion);
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
            // Comparar profesor y horario. Asume que Horario.equals() compara dia y bloque
            if (reserva.getProfesor().equals(profesor) && reserva.getHorario().equals(horario)) {
                return true;
            }
        }
        return false;
    }

    private void realizarReserva(Profesor profesor, Sala sala, Asignatura asignatura, Horario horario) {
        Reserva nuevaReserva = new Reserva(profesor, sala, asignatura, horario);

        // Opcional: verificar si la reserva ya existe con todos los detalles
        if (reservas.contains(nuevaReserva)) { // Requiere un buen .equals() en Reserva
            System.out.println("Error: Esta reserva ya existe. No se puede duplicar.");
            return;
        }

        reservas.add(nuevaReserva);
        sala.agregarHorarioOcupado(horario); // Añade el horario a la sala para marcarla como ocupada

        jsonDataManager.guardarReservas(reservas); // Guarda la lista de reservas
        salaControlador.guardarTodasLasSalasEnArchivo(); // Guarda las salas (con el horario ocupado actualizado)

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
            // Asegúrate de que toString() de Reserva, Profesor, Sala, Asignatura y Horario sean informativos
            System.out.printf("%d. Sala: %s | Profesor: %s | Asignatura: %s | Horario: %s, %s%n",
                    i + 1,
                    reserva.getSala().getNombre(),
                    reserva.getProfesor().getNombre(),
                    reserva.getAsignatura().getNombre(),
                    reserva.getHorario().getDia(),
                    reserva.getHorario().getBloque().getHoraInicio() + "-" + reserva.getHorario().getBloque().getHoraFin()); // Muestra el rango de horas
        }
    }

    public void cancelarAsignacion() {
        if (reservas.isEmpty()) {
            System.out.println("\nNo hay asignaciones para cancelar.");
            return;
        }

        verAsignaciones(); // Muestra la lista para que el usuario elija

        while (true) {
            System.out.print("\nSeleccione el número de la asignación a cancelar (0 para cancelar): ");
            String inputOpcion = scanner.nextLine().trim();
            if (inputOpcion.equals("0")) {
                System.out.println("Cancelación de asignación abortada.");
                return;
            }

            try {
                int opcion = Integer.parseInt(inputOpcion);
                if (opcion > 0 && opcion <= reservas.size()) {
                    Reserva reservaACancelar = reservas.remove(opcion - 1); // Elimina de la lista en memoria

                    // Eliminar el horario ocupado de la sala correspondiente
                    // Es importante que Sala tenga un buen equals() para Horario
                    reservaACancelar.getSala().eliminarHorarioOcupado(reservaACancelar.getHorario());

                    // Guardar los cambios en ambas listas
                    jsonDataManager.guardarReservas(reservas);
                    salaControlador.guardarTodasLasSalasEnArchivo(); // Persiste la sala con el horario liberado

                    System.out.println("\nAsignación cancelada exitosamente.");
                    System.out.println("Detalles de la reserva cancelada: " + reservaACancelar);
                    return;
                }
                System.out.println("Opción inválida. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }
}