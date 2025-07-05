package controlador;

import modelo.Profesor;
import modelo.Asignatura;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ProfesorControlador {
    private final Scanner scanner;
    private final JsonDataManager jsonDataManager;
    private List<Profesor> profesores;

    public ProfesorControlador() {
        this.scanner = new Scanner(System.in);
        this.jsonDataManager = new JsonDataManager();
        this.profesores = jsonDataManager.cargarProfesores();
    }

    public void registrarProfesor() {
        System.out.println("\n=== Registro de Profesor ===");

        String rut = obtenerRutProfesor();
        if (rut == null) return;

        System.out.print("Nombre del Profesor: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        System.out.print("Departamento: ");
        String departamento = scanner.nextLine().trim();
        if (departamento.isEmpty()) {
            System.out.println("El departamento no puede estar vacío.");
            return;
        }

        String id = generarIdUnico();
        Profesor nuevoProfesor = new Profesor(nombre, rut, departamento, id);

        if (registrarAsignaturas(nuevoProfesor)) {
            profesores.add(nuevoProfesor);
            jsonDataManager.guardarProfesores(profesores);
            System.out.println("\nProfesor registrado exitosamente:");
            System.out.println(nuevoProfesor);
        }
    }

    private String obtenerRutProfesor() {
        while (true) {
            System.out.print("RUT del Profesor (formato: 12345678-9): ");
            String rut = scanner.nextLine().trim();

            if (rut.isEmpty()) {
                System.out.println("El RUT no puede estar vacío.");
                return null;
            }

            if (!validarFormatoRut(rut)) {
                System.out.println("Formato de RUT inválido. Use el formato: 12345678-9");
                continue;
            }

            if (buscarProfesorPorRut(rut) != null) {
                System.out.println("Ya existe un profesor con este RUT.");
                return null;
            }

            return rut;
        }
    }

    private boolean validarFormatoRut(String rut) {
        return rut.matches("\\d{7,8}-[\\dkK]");
    }

    private String generarIdUnico() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private boolean registrarAsignaturas(Profesor profesor) {
        boolean asignaturasRegistradas = false;

        while (true) {
            System.out.println("\n=== Registro de Asignatura ===");
            System.out.println("Presione Enter sin ingresar datos para terminar");

            System.out.print("Nombre de la asignatura: ");
            String nombreAsignatura = scanner.nextLine().trim();
            if (nombreAsignatura.isEmpty()) {
                break;
            }

            System.out.print("Código de la asignatura (ej: ICC101): ");
            String codigo = scanner.nextLine().trim();
            if (!validarCodigoAsignatura(codigo)) {
                System.out.println("Formato de código inválido. Debe ser 3 letras seguidas de 3 números.");
                continue;
            }

            System.out.print("Carrera: ");
            String carrera = scanner.nextLine().trim();
            if (carrera.isEmpty()) {
                System.out.println("La carrera no puede estar vacía.");
                continue;
            }

            int semestre = obtenerNumeroPositivo("Semestre: ");
            if (semestre == -1) continue;

            int cantidadAlumnos = obtenerNumeroPositivo("Cantidad de alumnos: ");
            if (cantidadAlumnos == -1) continue;

            Asignatura nuevaAsignatura = new Asignatura(nombreAsignatura, codigo, carrera, semestre, cantidadAlumnos);
            profesor.agregarAsignatura(nuevaAsignatura);
            asignaturasRegistradas = true;
            System.out.println("Asignatura agregada exitosamente.");
        }

        if (!asignaturasRegistradas) {
            System.out.println("Debe registrar al menos una asignatura.");
            return false;
        }

        return true;
    }

    private boolean validarCodigoAsignatura(String codigo) {
        return codigo.matches("[A-Z]{3}\\d{3}");
    }

    private int obtenerNumeroPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int numero = Integer.parseInt(scanner.nextLine().trim());
                if (numero <= 0) {
                    System.out.println("El número debe ser positivo.");
                    return -1;
                }
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                return -1;
            }
        }
    }

    public Profesor buscarProfesorPorRut(String rut) {
        for (Profesor profesor : profesores) {
            if (profesor.getRut().equalsIgnoreCase(rut)) {
                return profesor;
            }
        }
        return null;
    }

    public List<Profesor> getProfesoresRegistrados() {
        return new ArrayList<>(profesores);
    }

    public void actualizarProfesor(Profesor profesor) {
        int index = -1;
        for (int i = 0; i < profesores.size(); i++) {
            if (profesores.get(i).getRut().equals(profesor.getRut())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            profesores.set(index, profesor);
            jsonDataManager.guardarProfesores(profesores);
        }
    }

    public boolean eliminarProfesor(String rut) {
        Profesor profesor = buscarProfesorPorRut(rut);
        if (profesor != null) {
            profesores.remove(profesor);
            jsonDataManager.guardarProfesores(profesores);
            return true;
        }
        return false;
    }
}