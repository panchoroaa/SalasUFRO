package controlador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import modelo.Profesor;
import modelo.Asignatura;

public class ProfesorControlador {
    private final Scanner scanner = new Scanner(System.in);
    private static final String NOMBRE_ARCHIVO = "BaseDatosProfesores.txt";

    public void registrarProfesor() {
        System.out.println("\n=== Registro de Profesor ===");
        System.out.print("Nombre del Profesor: ");
        String nombre = scanner.nextLine();

        System.out.print("Departamento: ");
        String departamento = scanner.nextLine();

        Profesor profe = new Profesor(nombre, departamento);

        while (true) {
            System.out.println("\n=== Registro de Asignatura ===");
            System.out.println("Ingrese los datos de la asignatura (o presione Enter para terminar):");

            System.out.print("Nombre de la asignatura: ");
            String nombreAsignatura = scanner.nextLine();

            if (nombreAsignatura.isEmpty()) {
                break;
            }

            System.out.print("Código de la asignatura (ej: ICC101): ");
            String codigo = scanner.nextLine();

            System.out.print("Carrera: ");
            String carrera = scanner.nextLine();

            int semestre;
            while (true) {
                try {
                    System.out.print("Semestre: ");
                    semestre = Integer.parseInt(scanner.nextLine());
                    if (semestre > 0) {
                        break;
                    } else {
                        System.out.println("El semestre debe ser un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, ingrese un número válido.");
                }
            }

            int cantidadAlumnos;
            while (true) {
                try {
                    System.out.print("Cantidad de alumnos: ");
                    cantidadAlumnos = Integer.parseInt(scanner.nextLine());
                    if (cantidadAlumnos >= 0) {
                        break;
                    } else {
                        System.out.println("La cantidad de alumnos no puede ser negativa.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, ingrese un número válido.");
                }
            }

            Asignatura nuevaAsignatura = new Asignatura(
                    nombreAsignatura,
                    codigo,
                    carrera,
                    semestre,
                    cantidadAlumnos
            );

            profe.agregarAsignatura(nuevaAsignatura);
            System.out.println("Asignatura agregada exitosamente.");
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