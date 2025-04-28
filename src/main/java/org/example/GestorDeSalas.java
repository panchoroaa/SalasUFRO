//Gestor de Salas SUFRO

package org.example;

import java.util.Scanner;

public class GestorDeSalas {

    public static class Sala {
        private String nombre;
        private String tamano;

        public Sala(String nombre, String tamano) {
            this.nombre = nombre;
            this.tamano = tamano;
        }

        public String getNombre() {
            return nombre;
        }
        public String getTamano() {
            return tamano;
        }
        @Override
        public String toString() {return "Nombre: "+nombre+" Tamaño: "+tamano;}
    }

    static Sala[] salas = {
            new Sala("Sala 1", "pequeña"),
            new Sala("Sala 2", "pequeña"),
            new Sala("Sala 3", "mediana"),
            new Sala("Sala 4", "mediana"),
            new Sala("Sala 5", "grande"),
            new Sala("Sala 6", "grande")
    };

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
                    filtrarSalas(scanner);
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

    public static String obtenerDiaSemana(int dia) {
        switch (dia) {
            case 1: return "Lunes";
            case 2: return "Martes";
            case 3: return "Miércoles";
            case 4: return "Jueves";
            case 5: return "Viernes";
            default: return "Día inválido";
        }
    }

    public static void filtrarSalas(Scanner scanner) {
        System.out.println("====== FILTRAR SALAS ======");

        System.out.println("Selecciona un día de la semana (1=Lunes ... 5=Viernes): ");
        int dia = scanner.nextInt();
        scanner.nextLine();

        if (dia < 1 || dia > 5) {
            System.out.println("Solo puedes agendar de lunes a viernes.");
            return;
        }

        System.out.print("Ingresa la hora (formato HH:MM): ");
        String hora = scanner.nextLine();

        System.out.print("¿Qué tamaño de sala deseas? (pequeña/mediana/grande): ");
        String tamano = scanner.nextLine();
        System.out.println("Buscando salas disponibles para:");
        System.out.println("Día: " + obtenerDiaSemana(dia));
        System.out.println("Hora: " + hora);
        System.out.println("Tamaño solicitado: " + tamano);
        System.out.println("Salas disponibles:");

        boolean encontrada = false;
        for (Sala sala : salas) {
            if (sala.getTamano().equalsIgnoreCase(tamano)) {
                System.out.println("- " + sala.getNombre());
                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("No se encontraron salas disponibles para ese tamaño.");
        }
    }

    public static class AgendarSalas {
        public static void main (String[]args){
            Scanner scannerSalas = new Scanner(System.in);
            int opcionSalas;
            do {
                System.out.println("====== MENÚ DE AGENDAMIENTO ======");
                System.out.println("Estas son las salas disponibles: ");
                for (Sala sala :salas){
                    System.out.println(sala);
                }

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


//