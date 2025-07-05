package controlador;

import modelo.Sala;
import modelo.Horario;
import modelo.BloqueHorario;
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

        String nombre = obtenerNombreSala();
        if (nombre == null) return;

        int capacidad = obtenerCapacidad();
        if (capacidad == -1) return;

        String estado = obtenerEstado();
        if (estado == null) return;

        Sala nuevaSala = new Sala(nombre, capacidad, estado, new ArrayList<>());
        salas.add(nuevaSala);
        jsonDataManager.guardarSalas(salas);

        System.out.println("\nSala registrada exitosamente:");
        System.out.println(nuevaSala);
    }

    private String obtenerNombreSala() {
        while (true) {
            System.out.print("Nombre de la Sala: ");
            String nombre = scanner.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return null;
            }

            if (buscarSalaPorNombre(nombre) != null) {
                System.out.println("Ya existe una sala con este nombre.");
                return null;
            }

            return nombre;
        }
    }

    private int obtenerCapacidad() {
        while (true) {
            System.out.print("Capacidad de la Sala: ");
            try {
                int capacidad = Integer.parseInt(scanner.nextLine().trim());
                if (capacidad <= 0) {
                    System.out.println("La capacidad debe ser un número positivo.");
                    continue;
                }
                return capacidad;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                return -1;
            }
        }
    }

    private String obtenerEstado() {
        System.out.print("Estado de la Sala (Disponible/Mantenimiento): ");
        String estado = scanner.nextLine().trim().toUpperCase();

        if (!estado.equals("DISPONIBLE") && !estado.equals("MANTENIMIENTO")) {
            System.out.println("Estado inválido. Use 'Disponible' o 'Mantenimiento'.");
            return null;
        }

        return estado;
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
        }
    }

    public void guardarTodasLasSalasEnArchivo() {
        jsonDataManager.guardarSalas(salas);
    }

    public boolean eliminarSala(String nombre) {
        Sala sala = buscarSalaPorNombre(nombre);
        if (sala != null) {
            salas.remove(sala);
            jsonDataManager.guardarSalas(salas);
            return true;
        }
        return false;
    }
}