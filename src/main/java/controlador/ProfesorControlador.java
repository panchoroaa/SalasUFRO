package controlador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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

    public void agregarAsignaturaSimple() {
        System.out.println("\n=== Agregar Asignatura a Profesor ===");

        List<Profesor> profesores = cargarProfesores();
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }

        Profesor profesor = seleccionarProfesor(profesores);
        if (profesor == null) {
            return;
        }

        System.out.print("Nombre de la asignatura: ");
        String nombreAsignatura = scanner.nextLine();

        if (nombreAsignatura == null || nombreAsignatura.trim().isEmpty()) {
            System.out.println("El nombre de la asignatura no puede estar vacío.");
            return;
        }

        int cantidadAlumnos;
        while (true) {
            try {
                System.out.print("Cantidad de alumnos: ");
                cantidadAlumnos = Integer.parseInt(scanner.nextLine());
                if (cantidadAlumnos > 0) {
                    break;
                } else {
                    System.out.println("La cantidad de alumnos debe ser mayor que 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        profesor.agregarAsignatura(nombreAsignatura.trim(), cantidadAlumnos);
        actualizarArchivoProfesores(profesores);
        System.out.println("Asignatura agregada exitosamente al profesor " + profesor.getNombre());
    }

    private List<Profesor> cargarProfesores() {
        List<Profesor> profesores = new ArrayList<>();
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("El archivo de profesores no existe: " + NOMBRE_ARCHIVO);
            return profesores;
        }

        try (Scanner fileScanner = new Scanner(archivo)) {
            while (fileScanner.hasNextLine()) {
                String linea = fileScanner.nextLine();
                try {
                    String[] partes = linea.split(", ");
                    String nombre = partes[0].substring("Nombre: ".length());
                    String departamento = partes[1].substring("Departamento: ".length());

                    Profesor profesor = new Profesor(nombre, departamento);

                    if (partes.length > 2 && partes[2].startsWith("Asignaturas: ")) {
                        String asignaturasStr = partes[2].substring("Asignaturas: ".length());
                        if (!asignaturasStr.equalsIgnoreCase("Ninguna")) {
                            String[] asignaturasConAlumnos = asignaturasStr.split("; ");
                            for (String asigAlumno : asignaturasConAlumnos) {
                                int openParen = asigAlumno.indexOf('(');
                                int closeParen = asigAlumno.indexOf(')');
                                if (openParen != -1 && closeParen != -1) {
                                    String nombreAsignatura = asigAlumno.substring(0, openParen).trim();
                                    int cantidad = Integer.parseInt(asigAlumno.substring(openParen + 1, closeParen).replace(" alumnos", ""));
                                    profesor.agregarAsignatura(nombreAsignatura, cantidad);
                                }
                            }
                        }
                    }
                    profesores.add(profesor);
                } catch (Exception e) {
                    System.err.println("Error al parsear línea de profesor: " + linea + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de profesores: " + e.getMessage());
        }
        return profesores;
    }

    private Profesor seleccionarProfesor(List<Profesor> profesores) {
        System.out.println("\nProfesores disponibles:");
        for (int i = 0; i < profesores.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, profesores.get(i).getNombre());
        }

        while (true) {
            System.out.print("Seleccione el número del profesor: ");
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                if (opcion > 0 && opcion <= profesores.size()) {
                    return profesores.get(opcion - 1);
                } else {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
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

    private void actualizarArchivoProfesores(List<Profesor> profesores) {
        try (FileWriter fileWriter = new FileWriter(NOMBRE_ARCHIVO, false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            for (Profesor profesor : profesores) {
                printWriter.println(profesor.toString());
            }
            System.out.println("Archivo de profesores actualizado exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al actualizar el archivo de profesores: " + e.getMessage());
        }
    }
}