package controlador;

import modelo.Sala;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalaControlador {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Sala> salasRegistradas = new ArrayList<>();
    private static final String NOMBRE_ARCHIVO_SALAS = "SalasRegistradas.txt";

    public SalaControlador() {
        cargarSalas();
    }

    public void registrarSala() {
        System.out.println("\n=== Registro de Sala ===");

        System.out.print("Nombre de la Sala: ");
        String nombre = scanner.nextLine();

        int capacidad;
        while (true) {
            System.out.print("Ingresa la capacidad de la sala: ");
            try {
                capacidad = Integer.parseInt(scanner.nextLine());
                if (capacidad <= 0) {
                    System.out.println("La capacidad debe ser un número positivo.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingresa un número válido para la capacidad.");
            }
        }

        Sala sala = new Sala(nombre, capacidad);
        salasRegistradas.add(sala);
        guardarTodasLasSalasEnArchivo();

        System.out.println("\nSala registrada exitosamente:");
        System.out.println(sala);
    }

    public List<Sala> getSalasRegistradas() {
        return new ArrayList<>(salasRegistradas);
    }

    public void guardarTodasLasSalasEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOMBRE_ARCHIVO_SALAS))) {
            for (Sala sala : salasRegistradas) {
                writer.println(sala.getNombre() + ";" + sala.getCapacidad());
            }
            System.out.println("Datos de salas guardados en " + NOMBRE_ARCHIVO_SALAS);
        } catch (IOException e) {
            System.err.println("Error al guardar las salas en el archivo: " + e.getMessage());
        }
    }

    public List<Sala> cargarSalas() {
        salasRegistradas.clear();
        File archivo = new File(NOMBRE_ARCHIVO_SALAS);

        if (!archivo.exists()) {
            System.out.println("El archivo de salas no existe: " + NOMBRE_ARCHIVO_SALAS);
            return new ArrayList<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    try {
                        String nombre = partes[0];
                        int capacidad = Integer.parseInt(partes[1]);
                        salasRegistradas.add(new Sala(nombre, capacidad));
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear la capacidad de la sala: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar las salas: " + e.getMessage());
        }

        return new ArrayList<>(salasRegistradas);
    }
}