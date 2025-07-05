package controlador;

import java.util.Scanner;
import modelo.BloqueHorario;

public class SelectorBloqueHorario {

    public static BloqueHorario seleccionarBloqueConOpcionCancelar() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Seleccione un bloque horario (0 para cancelar):");

        BloqueHorario[] bloques = BloqueHorario.values();
        for (int i = 0; i < bloques.length; i++) {
            System.out.printf("%d. %s%n", i + 1, bloques[i]);
        }

        while (true) {
            System.out.print("Opción: ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null; // Usuario canceló la selección
            }

            try {
                int opcion = Integer.parseInt(input);
                if (opcion >= 1 && opcion <= bloques.length) {
                    return bloques[opcion - 1];
                }
                System.out.println("Opción fuera de rango. Intente nuevamente.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    // Mantener el método original si es necesario
    public static BloqueHorario seleccionarBloque() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Seleccione un bloque horario:");

        BloqueHorario[] bloques = BloqueHorario.values();
        for (int i = 0; i < bloques.length; i++) {
            System.out.printf("%d. %s%n", i + 1, bloques[i]);
        }

        System.out.print("Opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());

        if (opcion < 1 || opcion > bloques.length) {
            throw new IllegalArgumentException("Opción fuera de rango.");
        }

        return bloques[opcion - 1];
    }
}