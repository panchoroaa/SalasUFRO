package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;

import java.util.Scanner;

public class MenuSalas {
    private final Scanner scanner = new Scanner(System.in);
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;
    private final SelectorMenu selector; // La instancia de SelectorMenu

    public MenuSalas() {
        this.profesorControlador = new ProfesorControlador();
        this.salaControlador = new SalaControlador();
        this.asignacionControlador = new AsignacionControlador(profesorControlador, salaControlador);
        this.selector = new SelectorMenu(profesorControlador, salaControlador, asignacionControlador);
    }

    public void iniciarMenu() {
        int opcion;
        do {
            mostrarMenu();
            opcion = obtenerOpcionUsuario();

            if (opcion == 6) { // Opción Salir
                if (confirmarSalida()) {
                    System.out.println("\n¡Gracias por usar el sistema!");
                } else {
                    opcion = -1; // Si no confirma, volvemos al menú (bucle continúa)
                }
            } else if (opcion >= 1 && opcion <= 5) { // Opciones válidas para ejecutar con selector
                selector.ejecutarOpcion(opcion);
                esperarEnter(); // Pausa después de cada operación (excepto salir)
            } else { // Opciones inválidas (ej. texto, número fuera de rango)
                System.out.println("Opción inválida. Por favor, ingrese un número entre 1 y 6.");
                esperarEnter(); // Pausa para que el usuario vea el mensaje
            }
        } while (opcion != 6); // El bucle continúa hasta que el usuario elige 6 y confirma
    }

    private void mostrarMenu() {
        limpiarPantalla();
        System.out.println("\n=== Sistema de Asignación de Salas ===");
        System.out.println("1. Registrar profesor");
        System.out.println("2. Registrar sala");
        System.out.println("3. Asignar sala a profesor");
        System.out.println("4. Modificar/cancelar asignación");
        System.out.println("5. Ver asignaciones");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private int obtenerOpcionUsuario() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Retorna -1 para indicar una entrada no numérica o inválida
        }
    }

    private boolean confirmarSalida() {
        System.out.print("\n¿Está seguro que desea salir? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        return respuesta.equals("S");
    }

    private void esperarEnter() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    private void limpiarPantalla() {
        // Intento de limpiar la pantalla, puede no funcionar en todos los IDEs o consolas
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // Fallback si no se puede limpiar la pantalla (por ejemplo, en algunos IDEs)
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }
}