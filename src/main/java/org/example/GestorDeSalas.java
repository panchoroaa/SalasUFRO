//Gestor de Salas SUFRO

package org.example;

import java.util.Scanner;

public class GestorDeSalas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("====== MENÚ PRINCIPAL ======");
            System.out.println("1. Filtrar Salas");
            System.out.println("2. Agendar Salas");
            System.out.println("3. Listar Salas");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    break;
                case 2:
                    AgendarSalas.main(null);
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida, intenta de nuevo.");
            }

            System.out.println();
        } while (opcion != 4);

        scanner.close();
    }

    public static class AgendarSalas {
        public static void main (String[]args){
            Scanner scannerSalas = new Scanner(System.in);
            int opcionSalas;
            do {
                System.out.println("====== MENÚ DE AGENDAMIENTO ======");
                System.out.println("Estas son las salas disponibles: ");
                System.out.println("1. Sala 1");
                System.out.println("2. Sala 2");
                System.out.println("3. Volver al menú principal");
                System.out.print("Selecciona una opción: ");
                opcionSalas = scannerSalas.nextInt();

                switch (opcionSalas) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        GestorDeSalas.main(null);
                        break;
                    default:
                        System.out.println("Opción inválida, intenta de nuevo.");
                }

                System.out.println();
            } while (opcionSalas != 3);

            scannerSalas.close();
        }


    }

}


