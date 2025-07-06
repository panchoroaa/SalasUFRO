package controlador;
import modelo.Profesor;
import modelo.Sala;
import modelo.Horario;
import modelo.BloqueHorario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AsignacionControlador {
    private final Scanner scanner = new Scanner(System.in);
    private static final String NOMBRE_ARCHIVO_PROFESORES = "BaseDatosProfesres.json";
    private final SalaControlador salaControlador;

    public AsignacionControlador() {
        this.salaControlador = new SalaControlador();
    }

    public void asignarSalaAProfesor() {
        System.out.println("\n=== Asignación de Sala a Profesor ===");

        List<Profesor> profesores = cargarProfesores();
        List<Sala> salas = salaControlador.getSalasRegistradas();

        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados. Registre profesores primero.");
            return;
        }

        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas. Registre salas primero.");
            return;
        }

        Profesor profesorSeleccionado = seleccionarProfesor(profesores);
        if (profesorSeleccionado == null) {
            System.out.println("Selección de profesor cancelada o inválida.");
            return;
        }

        int cantidadAlumnosRequerida = 0;
        String asignaturaSeleccionada = null;

        Map<String, Integer> asignaturasDelProfesor = profesorSeleccionado.getAsignaturasConAlumnos();
        if (asignaturasDelProfesor.isEmpty()) {
            System.out.println("El profesor seleccionado no tiene asignaturas registradas. No se puede asignar una sala.");
            return;
        }

        System.out.println("\nAsignaturas impartidas por " + profesorSeleccionado.getNombre() + ":");
        List<String> nombresAsignaturas = new ArrayList<>(asignaturasDelProfesor.keySet());
        for (int i = 0; i < nombresAsignaturas.size(); i++) {
            String nombreAsignatura = nombresAsignaturas.get(i);
            System.out.printf("%d. %s (%d alumnos)%n", i + 1, nombreAsignatura, asignaturasDelProfesor.get(nombreAsignatura));
        }

        while (true) {
            System.out.print("Seleccione el número de la asignatura a asignar la sala: ");
            try {
                int opcionAsignatura = Integer.parseInt(scanner.nextLine());
                if (opcionAsignatura > 0 && opcionAsignatura <= nombresAsignaturas.size()) {
                    asignaturaSeleccionada = nombresAsignaturas.get(opcionAsignatura - 1);
                    cantidadAlumnosRequerida = asignaturasDelProfesor.get(asignaturaSeleccionada);
                    break;
                } else {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }

        System.out.print("Ingrese el día para la asignación (ej. Lunes, Martes): ");
        String dia = scanner.nextLine();

        BloqueHorario bloqueRequerido = null;
        try {
            bloqueRequerido = SelectorBloqueHorario.seleccionarBloque();
        } catch (IllegalArgumentException e) {
            System.out.println("Error al seleccionar bloque horario: " + e.getMessage());
            return;
        }

        Horario horarioRequerido = new Horario(dia, bloqueRequerido);

        List<Sala> salasDisponiblesYCompatibles = new ArrayList<>();
        for (Sala sala : salas) {
            if (sala.getCapacidad() >= cantidadAlumnosRequerida && sala.estaDisponible(horarioRequerido)) {
                salasDisponiblesYCompatibles.add(sala);
            }
        }

        if (salasDisponiblesYCompatibles.isEmpty()) {
            System.out.println("\nNo se encontró ninguna sala disponible que cumpla con los requisitos (capacidad para " + cantidadAlumnosRequerida + " alumnos y libre en el horario " + horarioRequerido + ").");
            return;
        }

        System.out.println("\n--- Salas disponibles y compatibles para " + horarioRequerido + " (Capacidad >= " + cantidadAlumnosRequerida + ") ---");
        for (int i = 0; i < salasDisponiblesYCompatibles.size(); i++) {
            Sala currentSala = salasDisponiblesYCompatibles.get(i);
            System.out.printf("%d. %s (Capacidad: %d, Estado: %s)%n", i + 1, currentSala.getNombre(), currentSala.getCapacidad(), currentSala.getEstado());

            StringBuilder occupiedInfo = new StringBuilder();
            for (Horario h : currentSala.getHorariosOcupados()) {
                if (!h.conflictuaCon(horarioRequerido)) {
                    occupiedInfo.append(String.format("%s (%s); ", h.getDia(), h.getBloque()));
                }
            }
            if (occupiedInfo.length() > 0) {
                System.out.println("   Otros horarios ocupados: " + occupiedInfo.toString().trim());
            } else {
                System.out.println("   No tiene otros horarios ocupados.");
            }
        }

        Sala salaAsignada = null;
        while(true) {
            System.out.print("Seleccione el número de la sala que desea asignar (0 para cancelar): ");
            try {
                int opcionSala = Integer.parseInt(scanner.nextLine());
                if (opcionSala == 0) {
                    System.out.println("Asignación cancelada.");
                    return;
                }
                if (opcionSala > 0 && opcionSala <= salasDisponiblesYCompatibles.size()) {
                    salaAsignada = salasDisponiblesYCompatibles.get(opcionSala - 1);
                    break;
                } else {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        if (salaAsignada != null) {
            salaAsignada.agregarHorarioOcupado(horarioRequerido);
            System.out.println("\n--- Asignación Exitosa ---");
            System.out.printf("La sala '%s' ha sido asignada a '%s' para la asignatura '%s' (%d alumnos) el día '%s' en el bloque '%s'.%n",
                    salaAsignada.getNombre(), profesorSeleccionado.getNombre(), asignaturaSeleccionada, cantidadAlumnosRequerida, dia, bloqueRequerido);

            salaControlador.guardarTodasLasSalasEnArchivo();
        } else {
            System.out.println("\nError inesperado: No se pudo asignar la sala.");
        }
    }

    private List<Profesor> cargarProfesores() {
        List<Profesor> profesores = new ArrayList<>();
        File archivo = new File(NOMBRE_ARCHIVO_PROFESORES);
        if (!archivo.exists()) {
            System.out.println("El archivo de profesores no existe: " + NOMBRE_ARCHIVO_PROFESORES);
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
        } catch (FileNotFoundException e) {
            System.err.println("Archivo de profesores no encontrado: " + NOMBRE_ARCHIVO_PROFESORES);
        }
        return profesores;
    }

    private Profesor seleccionarProfesor(List<Profesor> profesores) {
        System.out.println("\nProfesores disponibles:");
        for (int i = 0; i < profesores.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, profesores.get(i).getNombre());
        }

        System.out.print("Seleccione el número del profesor: ");
        int opcion;
        while (true) {
            try {
                opcion = Integer.parseInt(scanner.nextLine());
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
}