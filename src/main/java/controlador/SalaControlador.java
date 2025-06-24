package controlador;

import modelo.Sala;
import modelo.BloqueHorario; // Still needed for parsing if old files exist or for future features
import modelo.Horario; // NEW: Needed to parse individual occupied schedules
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class SalaControlador {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Sala> salasRegistradas = new ArrayList<>(); // This will now reflect loaded data too
    private static final String NOMBRE_ARCHIVO_SALAS = "SalasRegistradas.txt";

    public SalaControlador() {
        cargarSalas(); // Carga las salas existentes cuando cargue
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

        String estadoInicial = "Disponible"; // Newly registered rooms are always available

        // When a sala is registered, it starts with no occupied blocks
        Sala sala = new Sala(nombre, capacidad, estadoInicial, new ArrayList<>());

        salasRegistradas.add(sala);
        guardarTodasLasSalasEnArchivo(); // Save all rooms, including the new one

        System.out.println("\nSala registrada exitosamente:");
        System.out.println(sala);
    }

    public List<Sala> getSalasRegistradas() {
        // Return a copy to prevent external modification of the internal list
        return new ArrayList<>(salasRegistradas);
    }

    // NEW: Method to save all salas (used for initial save and updates)
    public void guardarTodasLasSalasEnArchivo() {
        try (FileWriter fileWriter = new FileWriter(NOMBRE_ARCHIVO_SALAS, false); // Overwrite the file
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            for (Sala sala : salasRegistradas) {
                printWriter.println(sala.toString());
            }
            System.out.println("Datos de salas guardados en " + NOMBRE_ARCHIVO_SALAS);

        } catch (IOException e) {
            System.err.println("Error al guardar las salas en el archivo: " + e.getMessage());
        }
    }

    // NEW: Method to load salas from file (to be called by AsignacionControlador)
    public List<Sala> cargarSalas() {
        salasRegistradas.clear(); // Clear current list before loading
        File archivo = new File(NOMBRE_ARCHIVO_SALAS);
        if (!archivo.exists()) {
            System.out.println("El archivo de salas no existe: " + NOMBRE_ARCHIVO_SALAS);
            return new ArrayList<>();
        }

        try (Scanner fileScanner = new Scanner(archivo)) {
            while (fileScanner.hasNextLine()) {
                String linea = fileScanner.nextLine();
                try {
                    String nombre = extractValue(linea, "nombre='", "'");
                    int capacidad = Integer.parseInt(extractValue(linea, "capacidad=", ","));
                    String estado = extractValue(linea, "estado='", "'");

                    List<Horario> horariosOcupados = new ArrayList<>();
                    String horariosStr = extractValue(linea, "horariosOcupados=[", "]");

                    if (horariosStr != null && !horariosStr.isEmpty()) {
                        String[] horarioEntries = horariosStr.split("; ");
                        for (String entry : horarioEntries) {
                            String dia = extractValue(entry, "Día: ", ",");
                            String bloqueStr = extractValue(entry, "Bloque: ", "");

                            BloqueHorario bloque = null;
                            if (bloqueStr != null && !bloqueStr.equalsIgnoreCase("null")) {
                                String[] partesBloque = bloqueStr.split(" - ");
                                if (partesBloque.length == 2) {
                                    String horaInicio = partesBloque[0];
                                    String horaFin = partesBloque[1];
                                    for (BloqueHorario b : BloqueHorario.values()) {
                                        if (b.getHoraInicio().equals(horaInicio) && b.getHoraFin().equals(horaFin)) {
                                            bloque = b;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (dia != null && bloque != null) {
                                horariosOcupados.add(new Horario(dia, bloque));
                            }
                        }
                    }
                    salasRegistradas.add(new Sala(nombre, capacidad, estado, horariosOcupados));
                } catch (Exception e) {
                    System.err.println("Error al parsear línea de sala: " + linea + " - " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo de salas no encontrado: " + NOMBRE_ARCHIVO_SALAS);
        }
        return new ArrayList<>(salasRegistradas);
    }

    // Helper para extraer valores de las líneas del archivo
    private String extractValue(String line, String startDelimiter, String endDelimiter) {
        int startIndex = line.indexOf(startDelimiter);
        if (startIndex == -1) return null;
        startIndex += startDelimiter.length();
        int endIndex = -1;
        if (!endDelimiter.isEmpty()) {
            endIndex = line.indexOf(endDelimiter, startIndex);
        }

        if (endIndex == -1) { // If endDelimiter not found or is empty, extract until the end of the string
            return line.substring(startIndex).trim();
        }
        return line.substring(startIndex, endIndex).trim();
    }
}