package controlador;

import modelo.Sala;
import modelo.Horario; // Horario no se usa directamente aquí, pero es común tenerlo.
import modelo.BloqueHorario; // BloqueHorario tampoco se usa directamente.
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalaControlador {
    private final Scanner scanner;
    private final JsonDataManager jsonDataManager;
    private List<Sala> salas;

    public SalaControlador() {
        this.scanner = new Scanner(System.in);
        this.jsonDataManager = new JsonDataManager();
        this.salas = jsonDataManager.cargarSalas();
    }

    public void registrarSala() {
        System.out.println("\n=== Registro de Sala ===");
        System.out.println("Ingrese '0' en cualquier momento para cancelar y volver al menú principal.");

        String nombre = obtenerNombreSala();
        if (nombre == null) { // Cancelado o error de validación
            System.out.println("Registro de sala cancelado.");
            return;
        }

        Integer capacidad = obtenerCapacidad();
        if (capacidad == null) { // Cancelado o error de validación
            System.out.println("Registro de sala cancelado.");
            return;
        }

        String estado = obtenerEstado();
        if (estado == null) { // Cancelado o error de validación
            System.out.println("Registro de sala cancelado.");
            return;
        }

        Sala nuevaSala = new Sala(nombre, capacidad, estado, new ArrayList<>());
        salas.add(nuevaSala);
        jsonDataManager.guardarSalas(salas);

        System.out.println("\nSala registrada exitosamente:");
        System.out.println(nuevaSala);
    }

    // --- Métodos auxiliares para obtener entrada con opción de cancelar ---
    private String obtenerStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equals("0")) {
            return null; // Indica cancelación
        }
        return input;
    }

    private Integer obtenerIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                return null; // Indica cancelación
            }
            try {
                int numero = Integer.parseInt(input);
                if (numero <= 0) {
                    System.out.println("El número debe ser positivo.");
                } else {
                    return numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }


    private String obtenerNombreSala() {
        while (true) {
            String nombre = obtenerStringInput("Nombre de la Sala: ");
            if (nombre == null) { // Usuario ingresó '0'
                return null;
            }
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                continue;
            }
            if (buscarSalaPorNombre(nombre) != null) {
                System.out.println("Ya existe una sala con este nombre. Ingrese uno diferente o '0' para cancelar.");
                continue;
            }
            return nombre;
        }
    }

    private Integer obtenerCapacidad() {
        return obtenerIntInput("Capacidad de la Sala (ingrese '0' para cancelar): ");
    }


    private String obtenerEstado() {
        while (true) {
            String estado = obtenerStringInput("Estado de la Sala (Disponible/Mantenimiento, o '0' para cancelar): ");
            if (estado == null) { // Usuario ingresó '0'
                return null;
            }
            if (estado.isEmpty()) {
                System.out.println("El estado no puede estar vacío. Por favor, ingrese 'Disponible' o 'Mantenimiento'.");
                continue;
            }
            if (estado.equalsIgnoreCase("DISPONIBLE") || estado.equalsIgnoreCase("MANTENIMIENTO")) {
                return estado.substring(0, 1).toUpperCase() + estado.substring(1).toLowerCase(); // Capitaliza la primera letra
            } else {
                System.out.println("Estado inválido. Use 'Disponible' o 'Mantenimiento'.");
            }
        }
    }

    public Sala buscarSalaPorNombre(String nombre) {
        for (Sala sala : salas) {
            if (sala.getNombre().equalsIgnoreCase(nombre)) {
                return sala;
            }
        }
        return null;
    }

    public List<Sala> getSalasRegistradas() {
        return new ArrayList<>(salas);
    }

    public void actualizarSala(Sala sala) {
        int index = -1;
        for (int i = 0; i < salas.size(); i++) {
            if (salas.get(i).getNombre().equals(sala.getNombre())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            salas.set(index, sala);
            guardarTodasLasSalasEnArchivo();
            System.out.println("Sala actualizada exitosamente.");
        } else {
            System.out.println("Sala no encontrada para actualizar.");
        }
    }

    public void guardarTodasLasSalasEnArchivo() {
        jsonDataManager.guardarSalas(salas);
    }

    public boolean eliminarSala(String nombre) {
        Sala sala = buscarSalaPorNombre(nombre);
        if (sala != null) {
            // TODO: Antes de eliminar una sala, verificar si tiene reservas activas.
            // Si las tiene, no permitir la eliminación o pedir confirmación para cancelar reservas.
            salas.remove(sala);
            jsonDataManager.guardarSalas(salas);
            System.out.println("Sala eliminada exitosamente.");
            return true;
        }
        System.out.println("Sala con nombre '" + nombre + "' no encontrada.");
        return false;
    }
}