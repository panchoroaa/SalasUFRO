package vista;

import java.util.Scanner;

public class MenuVista {
    private final Scanner scanner = new Scanner(System.in);
    private final SelectorMenu selector = new SelectorMenu();

    public void iniciarMenu() {
        int opcion;
        do {
            mostrarMenu();
            opcion = obtenerOpcionUsuario();
            selector.ejecutarOpcion(opcion);
        } while (opcion != 7);
    }

    private void mostrarMenu() {
        System.out.println("\n=== Sistema de Asignación de Salas ===");
        System.out.println("1. Registrar profesor");
        System.out.println("2. Registrar sala");
        System.out.println("3. Asignar sala");
        System.out.println("4. Ver disponibilidad");
        System.out.println("5. Modificar/cancelar asignación");
        System.out.println("6. Ver asignaciones");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private int obtenerOpcionUsuario() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número.");
            return -1;
        }
    }
}
