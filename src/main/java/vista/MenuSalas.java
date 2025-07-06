// vista/MenuSalas.java
package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuSalas {
    private final Scanner scanner;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;
    private final SelectorMenu selector;

    public MenuSalas() {
        this.scanner = new Scanner(System.in);
        // La creación e inyección de controladores debe ser la única responsabilidad del constructor
        // y debe hacerse de forma clara.

        // Paso 1: Crear controladores que no tienen dependencias circulares (o aceptan null temporalmente)
        this.profesorControlador = new ProfesorControlador(null); // AsignacionControlador se inyectará después
        this.salaControlador = new SalaControlador(null);     // AsignacionControlador se inyectará después

        // Paso 2: Crear AsignacionControlador, que depende de los anteriores
        this.asignacionControlador = new AsignacionControlador(
                this.profesorControlador,
                this.salaControlador
        );

        // Paso 3: Completar las dependencias circulares usando setters
        this.profesorControlador.setAsignacionControlador(this.asignacionControlador);
        this.salaControlador.setAsignacionControlador(this.asignacionControlador);

        // Paso 4: Crear SelectorMenu, pasándole todas las dependencias necesarias
        this.selector = new SelectorMenu(
                this.scanner,
                this.profesorControlador,
                this.salaControlador,
                this.asignacionControlador
        );
    }

    public void iniciarMenu() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = obtenerOpcionMenuPrincipal(); // Método más específico

            if (opcion == 6) { // Opción Salir
                if (solicitarConfirmacionSalida()) { // Método más descriptivo
                    mostrarMensajeDespedida();
                } else {
                    opcion = -1; // Volver al menú
                }
            } else if (opcion >= 1 && opcion <= 5) {
                selector.ejecutarOpcion(opcion);
                pausarYContinuar(); // Método más genérico para "esperar enter"
            } else {
                mostrarMensajeErrorOpcionInvalida();
                pausarYContinuar();
            }
        } while (opcion != 6);
        cerrarRecursos(); // Cerrar el scanner y otros recursos si los hubiera
    }

    // Métodos atómicos para la UI del menú principal
    private void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("\n=== Sistema de Asignación de Salas ===");
        System.out.println("1. Gestión de Profesores");
        System.out.println("2. Gestión de Salas");
        System.out.println("3. Asignar sala a profesor");
        System.out.println("4. Modificar/cancelar asignación");
        System.out.println("5. Ver asignaciones");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private int obtenerOpcionMenuPrincipal() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                System.out.print("Seleccione una opción: "); // Volver a pedir
            }
        }
    }

    private boolean solicitarConfirmacionSalida() {
        System.out.print("\n¿Está seguro que desea salir? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S");
    }

    private void mostrarMensajeDespedida() {
        System.out.println("\n¡Gracias por usar el sistema!");
    }

    private void mostrarMensajeErrorOpcionInvalida() {
        System.out.println("Opción inválida. Por favor, ingrese un número entre 1 y 6.");
    }

    private void pausarYContinuar() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    private void limpiarPantalla() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // Fallback si no se puede limpiar la pantalla
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }

    private void cerrarRecursos() {
        scanner.close();
    }
}