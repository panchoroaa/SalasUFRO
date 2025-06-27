package controlador;

import java.util.Scanner;
import modelo.BloqueHorario;

public class SelectorBloqueHorario {

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
