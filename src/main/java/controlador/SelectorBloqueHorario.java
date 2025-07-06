package controlador;

import modelo.BloqueHorario;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SelectorBloqueHorario {
    private static final Scanner scanner = new Scanner(System.in);

    public static BloqueHorario seleccionarBloqueConOpcionCancelar() {
        System.out.println("\n--- Selección de Bloque Horario ---");
        BloqueHorario[] bloques = BloqueHorario.values();
        for (int i = 0; i < bloques.length; i++) {
            System.out.printf("%d. %s%n", i + 1, bloques[i].toString());
        }
        System.out.println("0. Cancelar");

        int opcion;
        while (true) {
            System.out.print("Seleccione un bloque horario: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                if (opcion == 0) {
                    return null; // El usuario eligió cancelar
                }
                if (opcion > 0 && opcion <= bloques.length) {
                    return bloques[opcion - 1];
                } else {
                    System.out.println("Opción no válida. Por favor, ingrese un número entre 0 y " + bloques.length + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Consumir la entrada inválida
            }
        }
    }
}