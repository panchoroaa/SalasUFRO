package controlador;

import modelo.EstadoSala;
import modelo.Sala;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Comparator; // For sorting

public class SalaControlador {
    private List<Sala> salas;
    private final JsonDataManager dataManager;
    private final Scanner scanner; // To receive input from the view layer

    public SalaControlador(JsonDataManager dataManager, Scanner scanner) {
        this.dataManager = dataManager;
        this.salas = dataManager.cargarSalas();
        this.scanner = scanner;
    }

    public List<Sala> getSalas() {
        return new ArrayList<>(salas); // Return a copy
    }

    public Optional<Sala> getSalaPorNombre(String nombre) {
        return salas.stream().filter(s -> s.getNombre().equalsIgnoreCase(nombre)).findFirst();
    }

    public void crearSala() {
        System.out.println("\n--- Crear Nueva Sala ---");
        System.out.print("Ingrese nombre de la sala: ");
        String nombre = scanner.nextLine().trim();

        if (getSalaPorNombre(nombre).isPresent()) {
            System.out.println("ERROR: Ya existe una sala con el nombre " + nombre + ".");
            return;
        }

        int capacidad;
        while (true) {
            try {
                System.out.print("Ingrese capacidad de la sala: ");
                capacidad = scanner.nextInt();
                if (capacidad <= 0) {
                    System.out.println("La capacidad debe ser un número positivo.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine(); // Clear invalid input
            }
        }
        scanner.nextLine(); // Consume newline

        EstadoSala estado = EstadoSala.DISPONIBLE; // Default state
        String estadoStr;
        while (true) {
            System.out.print("Ingrese estado inicial (DISPONIBLE, NO_DISPONIBLE) [default: DISPONIBLE]: ");
            estadoStr = scanner.nextLine().trim().toUpperCase();
            if (estadoStr.isEmpty()) {
                break; // Use default
            }
            try {
                estado = EstadoSala.valueOf(estadoStr);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Estado inválido. Intente de nuevo.");
            }
        }

        Sala nuevaSala = new Sala(nombre, capacidad);
        nuevaSala.setEstado(estado); // Set the chosen state
        salas.add(nuevaSala);
        dataManager.guardarSalas(salas);
        System.out.println("ÉXITO: Sala " + nombre + " creada exitosamente.");
    }

    public void listarSalas() {
        System.out.println("\n--- Listado de Salas ---");
        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }
        salas.stream()
                .sorted(Comparator.comparing(Sala::getNombre))
                .forEach(System.out::println);
    }

    public void actualizarSala() {
        System.out.println("\n--- Actualizar Sala ---");
        System.out.print("Ingrese nombre de la sala a actualizar (0 para cancelar): ");
        String nombre = scanner.nextLine().trim();
        if (nombre.equals("0")) {
            System.out.println("Operación cancelada.");
            return;
        }

        Optional<Sala> optionalSala = getSalaPorNombre(nombre);
        if (optionalSala.isPresent()) {
            Sala salaAActualizar = optionalSala.get();
            System.out.println("Sala actual: " + salaAActualizar);

            System.out.print("Ingrese nueva capacidad (0 para no cambiar): ");
            String capacidadStr = scanner.nextLine().trim();
            if (!capacidadStr.isEmpty() && !capacidadStr.equals("0")) {
                try {
                    int nuevaCapacidad = Integer.parseInt(capacidadStr);
                    if (nuevaCapacidad > 0) {
                        salaAActualizar.setCapacidad(nuevaCapacidad);
                    } else {
                        System.out.println("Capacidad debe ser positiva. No se cambió.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida para capacidad. No se cambió.");
                }
            }

            String estadoStr;
            while (true) {
                System.out.print("Ingrese nuevo estado (DISPONIBLE, NO_DISPONIBLE) [dejar en blanco para no cambiar]: ");
                estadoStr = scanner.nextLine().trim().toUpperCase();
                if (estadoStr.isEmpty()) {
                    break;
                }
                try {
                    EstadoSala nuevoEstado = EstadoSala.valueOf(estadoStr);
                    salaAActualizar.setEstado(nuevoEstado);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Estado inválido. Intente de nuevo.");
                }
            }

            dataManager.guardarSalas(salas);
            System.out.println("ÉXITO: Sala actualizada exitosamente.");
        } else {
            System.out.println("ERROR: Sala con nombre " + nombre + " no encontrada.");
        }
    }

    public void eliminarSala() {
        System.out.println("\n--- Eliminar Sala ---");
        System.out.print("Ingrese nombre de la sala a eliminar (0 para cancelar): ");
        String nombre = scanner.nextLine().trim();
        if (nombre.equals("0")) {
            System.out.println("Operación cancelada.");
            return;
        }

        // IMPORTANT: Before deleting a sala, you might want to check if it has active reservations.
        boolean removido = salas.removeIf(s -> s.getNombre().equalsIgnoreCase(nombre));

        if (removido) {
            dataManager.guardarSalas(salas);
            System.out.println("ÉXITO: Sala " + nombre + " eliminada exitosamente.");
        } else {
            System.out.println("ERROR: Sala con nombre " + nombre + " no encontrada.");
        }
    }
}