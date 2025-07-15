package vista;

import modelo.Asignatura;
import modelo.Profesor;
import modelo.Sala;
import modelo.EstadoSala; // Make sure this is imported if used directly
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.AsignaturaControlador; // New AsignaturaController
import controlador.AsignacionControlador;
// import controlador.CheckReserva; // Only if you need it here, but AsignacionControlador has it

import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.stream.Collectors;

public class SelectorMenu {
    private final Scanner scanner;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignaturaControlador asignaturaControlador; // New
    private final AsignacionControlador asignacionControlador;
    // private final CheckReserva checkReserva; // Removed as AsignacionControlador handles checks directly

    public SelectorMenu(Scanner scanner,
                        ProfesorControlador profesorControlador,
                        SalaControlador salaControlador,
                        AsignaturaControlador asignaturaControlador, // Add AsignaturaController
                        AsignacionControlador asignacionControlador) { // CheckReserva is removed for simplicity here
        this.scanner = scanner;
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.asignaturaControlador = asignaturaControlador; // Assign new controller
        this.asignacionControlador = asignacionControlador;
        // this.checkReserva = checkReserva; // Not directly used here, implied via AsignacionControlador
    }

    // --- Menus for each entity management ---

    public void mostrarMenuProfesores() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Profesores ---");
            System.out.println("1. Crear Profesor");
            System.out.println("2. Listar Profesores");
            System.out.println("3. Actualizar Profesor");
            System.out.println("4. Eliminar Profesor");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            opcion = leerOpcion(); // Use common leerOpcion

            switch (opcion) {
                case 1:
                    profesorControlador.crearProfesor(); // Delegate
                    break;
                case 2:
                    profesorControlador.listarProfesores(); // Delegate
                    break;
                case 3:
                    profesorControlador.actualizarProfesor(); // Delegate
                    break;
                case 4:
                    profesorControlador.eliminarProfesor(); // Delegate
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal de Gestión...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    public void mostrarMenuSalas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Salas ---");
            System.out.println("1. Crear Sala");
            System.out.println("2. Listar Salas");
            System.out.println("3. Actualizar Sala");
            System.out.println("4. Eliminar Sala");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    salaControlador.crearSala(); // Delegate
                    break;
                case 2:
                    salaControlador.listarSalas(); // Delegate
                    break;
                case 3:
                    salaControlador.actualizarSala(); // Delegate
                    break;
                case 4:
                    salaControlador.eliminarSala(); // Delegate
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal de Gestión...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    public void mostrarMenuAsignaturas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Asignaturas ---");
            System.out.println("1. Crear Asignatura");
            System.out.println("2. Listar Asignaturas");
            System.out.println("3. Actualizar Asignatura");
            System.out.println("4. Eliminar Asignatura");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    asignaturaControlador.crearAsignatura(); // Delegate
                    break;
                case 2:
                    asignaturaControlador.listarAsignaturas(); // Delegate
                    break;
                case 3:
                    asignaturaControlador.actualizarAsignatura(); // Delegate
                    break;
                case 4:
                    asignaturaControlador.eliminarAsignatura(); // Delegate
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal de Gestión...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // Common helper methods for reading input
    private int leerOpcion() {
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir newline
            return opcion;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
            scanner.nextLine(); // Clear the invalid input
            return -1; // Indicate invalid option
        }
    }

    private int solicitarSeleccion(String mensaje, int maxOpcion) {
        int seleccion;
        while (true) {
            System.out.print(mensaje + " ");
            try {
                seleccion = scanner.nextInt();
                scanner.nextLine();
                if (seleccion >= 0 && seleccion <= maxOpcion) {
                    return seleccion;
                } else {
                    System.out.println("Selección fuera de rango. Ingrese un número entre 0 y " + maxOpcion + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        System.out.println("\nÉXITO: " + mensaje);
    }

    private void mostrarMensajeError(String mensaje) {
        System.err.println("\nERROR: " + mensaje);
    }
}