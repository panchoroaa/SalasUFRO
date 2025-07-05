package controlador;

import modelo.Profesor;
import modelo.Asignatura;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern; // Para validación de RUT y Código de Asignatura

public class ProfesorControlador {
    private final Scanner scanner;
    private final JsonDataManager jsonDataManager;
    private List<Profesor> profesores;

    // Patrones de validación como constantes (buenas prácticas)
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d{7,8}-[kK\\d]$");
    private static final Pattern CODIGO_ASIGNATURA_PATTERN = Pattern.compile("^[a-zA-Z]{3}\\d{3}$"); // Ej: ICC101

    public ProfesorControlador() {
        this.scanner = new Scanner(System.in);
        this.jsonDataManager = new JsonDataManager();
        this.profesores = jsonDataManager.cargarProfesores();
    }

    public void registrarProfesor() {
        System.out.println("\n=== Registro de Profesor ===");
        System.out.println("Ingrese '0' en cualquier momento para cancelar y volver al menú principal.");

        String rut = obtenerRutProfesor();
        if (rut == null) { // Cancelado o error de validación
            System.out.println("Registro de profesor cancelado.");
            return;
        }

        String nombre = obtenerStringInput("Nombre del Profesor: ");
        if (nombre == null) {
            System.out.println("Registro de profesor cancelado.");
            return;
        }
        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío. Registro cancelado.");
            return;
        }

        String departamento = obtenerStringInput("Departamento: ");
        if (departamento == null) {
            System.out.println("Registro de profesor cancelado.");
            return;
        }
        if (departamento.isEmpty()) {
            System.out.println("El departamento no puede estar vacío. Registro cancelado.");
            return;
        }

        String id = generarIdUnico();
        Profesor nuevoProfesor = new Profesor(nombre, rut, departamento, id);

        // Intenta registrar asignaturas. Si el usuario cancela o no registra ninguna válida, no se guarda el profesor.
        if (!registrarAsignaturasParaProfesor(nuevoProfesor)) {
            System.out.println("Registro de profesor cancelado o no se agregaron asignaturas válidas.");
            return;
        }

        profesores.add(nuevoProfesor);
        jsonDataManager.guardarProfesores(profesores);
        System.out.println("\nProfesor registrado exitosamente:");
        System.out.println(nuevoProfesor);
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


    private String obtenerRutProfesor() {
        while (true) {
            System.out.print("RUT del Profesor (formato: 12345678-9, o '0' para cancelar): ");
            String rut = scanner.nextLine().trim();

            if (rut.equals("0")) {
                return null; // Cancelar
            }

            if (rut.isEmpty()) {
                System.out.println("El RUT no puede estar vacío.");
                continue;
            }

            if (!RUT_PATTERN.matcher(rut).matches()) {
                System.out.println("Formato de RUT inválido. Use el formato: 12345678-9 (7 u 8 dígitos, guion, dígito/K).");
                continue;
            }

            if (buscarProfesorPorRut(rut) != null) {
                System.out.println("Ya existe un profesor con este RUT. Ingrese uno diferente o '0' para cancelar.");
                continue;
            }
            return rut;
        }
    }

    private String generarIdUnico() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Renombrado para mayor claridad, ahora es una etapa del registro del profesor
    private boolean registrarAsignaturasParaProfesor(Profesor profesor) {
        boolean alMenosUnaAsignaturaAgregada = false;

        System.out.println("\n=== Registro de Asignaturas para " + profesor.getNombre() + " ===");
        System.out.println("Ingrese '0' en cualquier campo para cancelar el registro del profesor.");
        System.out.println("Presione Enter sin ingresar datos en el 'Nombre de la asignatura' para terminar de añadir asignaturas.");

        while (true) {
            System.out.println("\n--- Nueva Asignatura ---");
            String nombreAsignatura = obtenerStringInput("Nombre de la asignatura: ");

            if (nombreAsignatura == null) { // Usuario ingresó '0'
                return false; // Cancela el registro del profesor completamente
            }
            if (nombreAsignatura.isEmpty()) { // Usuario presionó Enter para terminar
                if (!alMenosUnaAsignaturaAgregada) {
                    System.out.println("Debe registrar al menos una asignatura para el profesor.");
                    continue; // Vuelve a pedir la asignatura
                }
                System.out.println("Finalizando registro de asignaturas.");
                break; // Terminar el bucle de asignaturas
            }

            String codigo = obtenerCodigoAsignatura();
            if (codigo == null) { // Cancelado o error de validación
                return false; // Cancela el registro del profesor
            }
            // Comprobar duplicados de código de asignatura para el mismo profesor
            boolean codigoDuplicadoParaProfesor = false;
            for (Asignatura a : profesor.getAsignaturasImpartidas()) {
                if (a.getCodigo().equalsIgnoreCase(codigo)) {
                    codigoDuplicadoParaProfesor = true;
                    System.out.println("Error: El profesor ya tiene una asignatura con el código " + codigo + ".");
                    break;
                }
            }
            if (codigoDuplicadoParaProfesor) {
                continue; // Vuelve a pedir la asignatura actual
            }


            String carrera = obtenerStringInput("Carrera: ");
            if (carrera == null) return false; // Cancelado
            if (carrera.isEmpty()) {
                System.out.println("La carrera no puede estar vacía.");
                continue;
            }

            Integer semestre = obtenerIntInput("Semestre: ");
            if (semestre == null) return false; // Cancelado

            Integer cantidadAlumnos = obtenerIntInput("Cantidad de alumnos: ");
            if (cantidadAlumnos == null) return false; // Cancelado

            Asignatura nuevaAsignatura = new Asignatura(nombreAsignatura, codigo, carrera, semestre, cantidadAlumnos);
            profesor.agregarAsignatura(nuevaAsignatura);
            alMenosUnaAsignaturaAgregada = true;
            System.out.println("Asignatura '" + nombreAsignatura + "' agregada exitosamente.");
        }
        return true; // Se agregaron asignaturas o el usuario decidió terminar correctamente
    }

    private String obtenerCodigoAsignatura() {
        while (true) {
            String codigo = obtenerStringInput("Código de la asignatura (ej: ICC101, o '0' para cancelar): ");
            if (codigo == null) { // Usuario ingresó '0'
                return null;
            }
            if (codigo.isEmpty()) {
                System.out.println("El código no puede estar vacío.");
                continue;
            }
            if (!CODIGO_ASIGNATURA_PATTERN.matcher(codigo).matches()) {
                System.out.println("Formato de código inválido. Debe ser 3 letras seguidas de 3 números (ej: ICC101).");
                continue;
            }
            return codigo;
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
            System.out.println("Profesor actualizado exitosamente.");
        } else {
            System.out.println("Profesor no encontrado para actualizar.");
        }
    }

    public boolean eliminarProfesor(String rut) {
        Profesor profesor = buscarProfesorPorRut(rut);
        if (profesor != null) {
            // TODO: Antes de eliminar un profesor, verificar si tiene reservas activas.
            // Si las tiene, no permitir la eliminación o pedir confirmación para cancelar reservas.
            profesores.remove(profesor);
            jsonDataManager.guardarProfesores(profesores);
            System.out.println("Profesor eliminado exitosamente.");
            return true;
        }
        System.out.println("Profesor con RUT '" + rut + "' no encontrado.");
        return false;
    }
}