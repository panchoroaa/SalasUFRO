package controlador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import modelo.Profesor;

public class ProfesorControlador {
    private final Scanner scanner = new Scanner(System.in);
    private static final String NOMBRE_ARCHIVO = "BaseDatosProfesores.txt";

    public void registrarProfesor() {
        System.out.println("\n=== Registro de Profesor ===");
        System.out.print("Nombre del Profesor: ");
        String nombre = scanner.nextLine();

        System.out.print("Departamento: ");
        String departamento = scanner.nextLine();

        System.out.println("Ingresa las asignaturas que imparte el profesor y el módulo, separadas por coma (ej. Matematicas 1, Fisica 3, Programacion 8):");
        String asignaturasInput = scanner.nextLine();

        String[] asignaturasArray = asignaturasInput.split(",");

        Profesor profe = new Profesor(nombre, departamento);

        for (String asignatura : asignaturasArray) {
            String nombreAsignatura = asignatura.trim();
            if (!nombreAsignatura.isEmpty()) {
                int cantidadAlumnos;
                while (true) {
                    System.out.print("Ingresa la cantidad de alumnos para '" + nombreAsignatura + "': ");
                    try {
                        cantidadAlumnos = Integer.parseInt(scanner.nextLine());
                        if (cantidadAlumnos < 0) {
                            System.out.println("La cantidad de alumnos no puede ser negativa. Intenta de nuevo.");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Por favor, ingresa un número entero.");
                    }
                }

                profe.agregarAsignatura(nombreAsignatura, cantidadAlumnos);
            }
        }

        guardarProfesorEnArchivo(profe);

        System.out.println("\nProfesor registrado exitosamente:");
        System.out.println(profe);
    }

    private void guardarProfesorEnArchivo(Profesor profesor) {
        try (FileWriter fileWriter = new FileWriter(NOMBRE_ARCHIVO, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(profesor.toString());
            System.out.println("Datos del profesor guardados en " + NOMBRE_ARCHIVO);

        } catch (IOException e) {
            System.err.println("Error al guardar el profesor en el archivo: " + e.getMessage());
        }
    }
}