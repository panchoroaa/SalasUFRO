package controlador;

import modelo.Sala;
import modelo.SalaPequena;
import modelo.SalaMediana;
import modelo.SalaGrande;

import java.util.Scanner;

public class SalaControlador {
    private final Scanner scanner = new Scanner(System.in);

    public void registrarSala() {
        System.out.println("\n=== Registro de Sala ===");

        System.out.print("ID: ");
        String id = scanner.nextLine();

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.println("Seleccione tamaño:");
        System.out.println("1. Pequeña");
        System.out.println("2. Mediana");
        System.out.println("3. Grande");
        System.out.print("Opción: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida.");
            return;
        }

        Sala sala;

        switch (opcion) {
            case 1 -> sala = new SalaPequena(id, nombre);
            case 2 -> sala = new SalaMediana(id, nombre);
            case 3 -> sala = new SalaGrande(id, nombre);
            default -> {
                System.out.println("Opción inválida.");
                return;
            }
        }

        // Aquí podrías guardar en archivo o lista más adelante
        System.out.println("Sala registrada:");
        System.out.println(sala);
    }
}
