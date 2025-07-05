package vista;

import java.util.Scanner;

public class MenuSalas {
    private final Scanner scanner = new Scanner(System.in);
    private final SelectorMenu selector = new SelectorMenu();

    public void iniciarMenu() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = obtenerOpcionUsuario();

            if (opcion == 6) {
                if (confirmarSalida()) {
                    System.out.println("\n¡Gracias por usar el sistema!");
                    continuar = false;
                }
            } else if (opcion > 0) {
                selector.ejecutarOpcion(opcion);
                esperarEnter();
            }
        }
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
            int opcion = Integer.parseInt(input);
            if (opcion < 1 || opcion > 6) {
                System.out.println("Por favor, ingrese un número entre 1 y 6.");
                return -1;
            }
            return opcion;
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return -1;
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}