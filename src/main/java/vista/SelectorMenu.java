// vista/SelectorMenu.java
package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.CheckReserva;
import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.Profesor;
import modelo.Sala;
import modelo.EstadoSala;
import modelo.RutNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class SelectorMenu {
    private final Scanner scanner;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;
    private final CheckReserva checkReserva;

    public SelectorMenu(Scanner scanner,
                        ProfesorControlador profesorControlador,
                        SalaControlador salaControlador,
                        AsignacionControlador asignacionControlador,
                        CheckReserva checkReserva) {
        this.scanner = scanner;
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.asignacionControlador = asignacionControlador;
        this.checkReserva = checkReserva;
    }

    public void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n--- Menú Principal ---");
            System.out.println("1. Gestión de Profesores");
            System.out.println("2. Gestión de Salas");
            System.out.println("3. Asignación de Salas");
            System.out.println("4. Listar Asignaciones");
            System.out.println("5. Cancelar Asignación");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1: mostrarMenuProfesores(); break;
                case 2: mostrarMenuSalas(); break;
                case 3: realizarAsignacionFlow(); break;
                case 4: asignacionControlador.listarAsignaciones(); break;
                case 5: cancelarAsignacionFlow(); break;
                case 0: System.out.println("Saliendo del programa. ¡Hasta luego!"); break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuProfesores() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Profesores ---");
            System.out.println("1. Registrar Profesor");
            System.out.println("2. Listar Profesores");
            System.out.println("3. Actualizar Profesor");
            System.out.println("4. Eliminar Profesor");
            System.out.println("5. Asignar Asignatura a Profesor");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1: registrarProfesorFlow(); break;
                case 2: profesorControlador.listarProfesores(); break;
                case 3: actualizarProfesorFlow(); break;
                case 4: eliminarProfesorFlow(); break;
                case 5: asignarAsignaturaAProfesorFlow(); break;
                case 0: break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuSalas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Salas ---");
            System.out.println("1. Registrar Sala");
            System.out.println("2. Listar Salas");
            System.out.println("3. Actualizar Estado de Sala");
            System.out.println("4. Eliminar Sala");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1: registrarSalaFlow(); break;
                case 2: salaControlador.listarSalas(); break;
                case 3: actualizarEstadoSalaFlow(); break;
                case 4: eliminarSalaFlow(); break;
                case 0: break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void registrarProfesorFlow() {
        System.out.println("\n=== Registro de Profesor ===");
        String nombre = solicitarNombreProfesor();
        if (nombre == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }

        String rut = solicitarRutProfesor();
        if (rut == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }

        if (checkReserva.existeProfesor(rut)) {
            System.out.println("Error: Ya existe un profesor con este RUT. Ingrese uno diferente.");
            return;
        }

        String departamento = solicitarDepartamentoProfesor();
        if (departamento == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }

        Profesor nuevoProfesor = profesorControlador.crearProfesor(nombre, rut, departamento);
        if (nuevoProfesor != null) {
            mostrarMensajeExito("Profesor registrado exitosamente:\n" + nuevoProfesor.toString());
        } else {
            mostrarMensajeError("Error al registrar el profesor.");
        }
    }

    private void actualizarProfesorFlow() {
        System.out.println("\n=== Actualizar Profesor ===");
        profesorControlador.listarProfesores();
        if (profesorControlador.getProfesoresRegistrados().isEmpty()) {
            mostrarNoHayElementosPara("profesores para actualizar");
            return;
        }

        String rut = solicitarRutProfesor();
        if (rut == null) { mostrarCancelacionOperacion("Actualización de profesor"); return; }

        Profesor profesorAActualizar = checkReserva.obtenerProfesorPorRut(rut);
        if (profesorAActualizar == null) {
            System.out.println("Error: No se encontró un profesor con el RUT especificado.");
            return;
        }

        System.out.println("Profesor encontrado: " + profesorAActualizar.getNombre());
        System.out.println("Ingrese el nuevo nombre (deje vacío para mantener el actual):");
        String nuevoNombre = scanner.nextLine().trim();

        System.out.println("Ingrese el nuevo departamento (deje vacío para mantener el actual):");
        String nuevoDepartamento = scanner.nextLine().trim();

        boolean actualizado = profesorControlador.actualizarProfesor(profesorAActualizar, nuevoNombre, nuevoDepartamento);
        if (actualizado) {
            mostrarMensajeExito("Profesor actualizado exitosamente.");
        } else {
            mostrarMensajeError("No se pudo actualizar el profesor.");
        }
    }

    private void eliminarProfesorFlow() {
        System.out.println("\n=== Eliminar Profesor ===");
        profesorControlador.listarProfesores();
        if (profesorControlador.getProfesoresRegistrados().isEmpty()) {
            mostrarNoHayElementosPara("profesores para eliminar");
            return;
        }

        String rutAEliminar = solicitarRutProfesor();
        if (rutAEliminar == null) { mostrarCancelacionOperacion("Eliminación de profesor"); return; }

        Profesor profesorAEliminar = checkReserva.obtenerProfesorPorRut(rutAEliminar);
        if (profesorAEliminar == null) {
            System.out.println("Error: No se encontró un profesor con el RUT especificado.");
            return;
        }

        if (checkReserva.profesorTieneReservasActivas(profesorAEliminar)) {
            mostrarMensajeError("Error: No se puede eliminar el profesor porque tiene asignaciones activas.");
            return;
        }

        boolean eliminado = profesorControlador.eliminarProfesor(rutAEliminar);
        if (eliminado) {
            mostrarMensajeExito("Profesor eliminado exitosamente.");
        } else {
            mostrarMensajeError("Error inesperado al eliminar el profesor.");
        }
    }

    private void asignarAsignaturaAProfesorFlow() {
        System.out.println("\n=== Asignar Asignatura a Profesor ===");
        profesorControlador.listarProfesores();
        if (profesorControlador.getProfesoresRegistrados().isEmpty()) {
            mostrarNoHayElementosPara("profesores para asignar asignaturas");
            return;
        }

        String rut = solicitarRutProfesor();
        if (rut == null) { mostrarCancelacionOperacion("Asignación de asignatura"); return; }

        Profesor profesor = checkReserva.obtenerProfesorPorRut(rut);
        if (profesor == null) {
            System.out.println("Error: No se encontró un profesor con el RUT especificado.");
            return;
        }

        System.out.println("Asignaturas que el profesor " + profesor.getNombre() + " ya imparte:");
        if (profesor.getAsignaturasImpartidas().isEmpty()) {
            System.out.println("  Ninguna.");
        } else {
            profesor.getAsignaturasImpartidas().forEach(a -> System.out.println("  - " + a.getNombre() + " (" + a.getCodigo() + ")"));
        }

        List<Asignatura> todasAsignaturas = profesorControlador.getTodasLasAsignaturasDisponibles();
        if (todasAsignaturas.isEmpty()) {
            mostrarNoHayElementosPara("asignaturas disponibles");
            return;
        }

        List<Asignatura> asignaturasDisponibles = profesorControlador.getAsignaturasDisponiblesParaAsignar(profesor, todasAsignaturas);
        if (asignaturasDisponibles.isEmpty()) {
            System.out.println("No hay más asignaturas disponibles para asignar a este profesor.");
            return;
        }

        System.out.println("\nAsignaturas disponibles para asignar:");
        for (int i = 0; i < asignaturasDisponibles.size(); i++) {
            System.out.printf("%d. %s (%s) - %d alumnos%n",
                    i + 1, asignaturasDisponibles.get(i).getNombre(),
                    asignaturasDisponibles.get(i).getCodigo(),
                    asignaturasDisponibles.get(i).getCantidadAlumnos());
        }

        int seleccionAsignatura = solicitarSeleccion("Seleccione la asignatura a asignar (0 para cancelar):", asignaturasDisponibles.size());
        if (seleccionAsignatura == 0) { mostrarCancelacionOperacion("Asignación de asignatura"); return; }

        Asignatura asignaturaAAsignar = asignaturasDisponibles.get(seleccionAsignatura - 1);

        boolean asignado = profesorControlador.asignarAsignaturaAProfesor(profesor, asignaturaAAsignar);
        if (asignado) {
            mostrarMensajeExito("Asignatura asignada exitosamente.");
        } else {
            mostrarMensajeError("Error: El profesor ya imparte esta asignatura o hubo un error inesperado.");
        }
    }

    private void registrarSalaFlow() {
        System.out.println("\n=== Registro de Sala ===");
        String nombre = solicitarNombreSala();
        if (nombre == null) { mostrarCancelacionOperacion("Registro de sala"); return; }

        if (checkReserva.existeSala(nombre)) {
            System.out.println("Error: Ya existe una sala con este nombre. Ingrese uno diferente.");
            return;
        }

        Integer capacidad = solicitarCapacidadSala();
        if (capacidad == null) { mostrarCancelacionOperacion("Registro de sala"); return; }

        Sala nuevaSala = salaControlador.crearSala(nombre, capacidad);
        if (nuevaSala != null) {
            mostrarMensajeExito("Sala registrada exitosamente:\n" + nuevaSala.toString());
        } else {
            mostrarMensajeError("Error al registrar la sala.");
        }
    }

    private void actualizarEstadoSalaFlow() {
        System.out.println("\n=== Actualizar Estado de Sala ===");
        salaControlador.listarSalas();
        if (salaControlador.getSalasRegistradas().isEmpty()) {
            mostrarNoHayElementosPara("salas para actualizar su estado");
            return;
        }

        String nombreSala = solicitarNombreSala();
        if (nombreSala == null) { mostrarCancelacionOperacion("Actualización de estado de sala"); return; }

        Sala salaAActualizar = checkReserva.obtenerSalaPorNombre(nombreSala);
        if (salaAActualizar == null) {
            System.out.println("Error: No se encontró una sala con el nombre especificado.");
            return;
        }

        System.out.println("Estado actual de " + salaAActualizar.getNombre() + ": " + salaControlador.getEstadoSala(salaAActualizar));
        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. DISPONIBLE");
        System.out.println("2. EN_MANTENIMIENTO");
        System.out.println("0. Cancelar");
        int opcionEstado = leerOpcion();

        EstadoSala nuevoEstado = null;
        switch (opcionEstado) {
            case 1: nuevoEstado = EstadoSala.DISPONIBLE; break;
            case 2: nuevoEstado = EstadoSala.EN_MANTENIMIENTO; break;
            case 0: mostrarCancelacionOperacion("Actualización de estado de sala"); return;
            default: System.out.println("Opción de estado no válida."); return;
        }

        boolean actualizado = salaControlador.actualizarEstadoSala(salaAActualizar, nuevoEstado);
        if (actualizado) {
            mostrarMensajeExito("Estado de sala actualizado exitosamente.");
        } else {
            mostrarMensajeError("No se pudo actualizar el estado de la sala.");
        }
    }

    private void eliminarSalaFlow() {
        System.out.println("\n=== Eliminar Sala ===");
        salaControlador.listarSalas();
        if (salaControlador.getSalasRegistradas().isEmpty()) {
            mostrarNoHayElementosPara("salas para eliminar");
            return;
        }

        String nombreAEliminar = solicitarNombreSala();
        if (nombreAEliminar == null) { mostrarCancelacionOperacion("Eliminación de sala"); return; }

        Sala salaAEliminar = checkReserva.obtenerSalaPorNombre(nombreAEliminar);
        if (salaAEliminar == null) {
            System.out.println("Error: No se encontró una sala con el nombre especificado.");
            return;
        }

        if (checkReserva.salaTieneReservasActivas(salaAEliminar)) {
            mostrarMensajeError("Error: No se puede eliminar la sala porque tiene asignaciones activas.");
            return;
        }

        boolean eliminado = salaControlador.eliminarSala(nombreAEliminar);
        if (eliminado) {
            mostrarMensajeExito("Sala eliminada exitosamente.");
        } else {
            mostrarMensajeError("Error inesperado al eliminar la sala.");
        }
    }

    private void realizarAsignacionFlow() {
        System.out.println("\n=== Realizar Asignación de Sala ===");

        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        if (profesores.isEmpty()) { mostrarNoHayElementosPara("profesores para asignar salas"); return; }
        Profesor profesor = seleccionarProfesorDeLista(profesores, "asignar sala");
        if (profesor == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        List<Asignatura> asignaturasProfesor = profesorControlador.getAsignaturasImpartidasPorProfesor(profesor);
        if (asignaturasProfesor.isEmpty()) { System.out.println("Error: El profesor seleccionado no imparte asignaturas. No se puede realizar la asignación."); return; }
        Asignatura asignatura = seleccionarAsignaturaDeLista(asignaturasProfesor, "asignar sala");
        if (asignatura == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        List<Sala> salas = salaControlador.getSalasRegistradas();
        if (salas.isEmpty()) { mostrarNoHayElementosPara("salas para asignar"); return; }
        List<Sala> salasConCapacidad = salas.stream()
                .filter(s -> s.getCapacidad() >= asignatura.getCantidadAlumnos())
                .toList();
        if (salasConCapacidad.isEmpty()) { System.out.println("No hay salas disponibles con capacidad suficiente para " + asignatura.getCantidadAlumnos() + " alumnos."); return; }
        Sala sala = seleccionarSalaDeLista(salasConCapacidad, "asignar");
        if (sala == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        String dia = solicitarDiaSemana();
        if (dia == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }
        BloqueHorario bloque = solicitarBloqueHorario();
        if (bloque == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        if (!checkReserva.salaEstaDisponible(sala, dia, bloque)) {
            mostrarMensajeError("Error: La sala " + sala.getNombre() + " no está disponible en el horario " + dia + " " + bloque + " o está en mantenimiento.");
            return;
        }
        if (checkReserva.profesorTieneConflictoHorario(profesor, dia, bloque)) {
            mostrarMensajeError("Error: El profesor " + profesor.getNombre() + " ya tiene una asignación en el horario " + dia + " " + bloque + ".");
            return;
        }

        String resultado = asignacionControlador.realizarAsignacion(profesor, sala, asignatura, dia, bloque);
        System.out.println(resultado);
    }

    private void cancelarAsignacionFlow() {
        System.out.println("\n=== Cancelar Asignación de Sala ===");
        List<String> asignaciones = asignacionControlador.getReservasParaUI();
        if (asignaciones.isEmpty()) {
            mostrarNoHayElementosPara("asignaciones para cancelar");
            return;
        }

        for (int i = 0; i < asignaciones.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, asignaciones.get(i));
        }

        int index = solicitarSeleccion("Seleccione el número de la asignación a cancelar (0 para cancelar):", asignaciones.size());
        if (index == 0) { mostrarCancelacionOperacion("Cancelación de asignación"); return; }

        String resultado = asignacionControlador.cancelarAsignacion(index - 1);
        System.out.println(resultado);
    }

    private int leerOpcion() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada no válida. Por favor, ingrese un número.");
            scanner.next();
            System.out.print("Seleccione una opción: ");
        }
        int opcion = scanner.nextInt();
        scanner.nextLine();
        return opcion;
    }

    private String solicitarNombreProfesor() { return solicitarEntrada("Ingrese el nombre del profesor (0 para cancelar): "); }
    private String solicitarRutProfesor() { return solicitarEntrada("Ingrese el RUT del profesor (ej. 12345678-9) (0 para cancelar): "); }
    private String solicitarDepartamentoProfesor() { return solicitarEntrada("Ingrese el departamento del profesor (0 para cancelar): "); }
    private String solicitarNombreSala() { return solicitarEntrada("Ingrese el nombre de la sala (0 para cancelar): "); }
    private Integer solicitarCapacidadSala() {
        while (true) {
            String input = solicitarEntrada("Ingrese la capacidad de la sala (número entero positivo, 0 para cancelar): ");
            if (input == null) return null;
            try {
                int capacidad = Integer.parseInt(input);
                if (capacidad > 0) return capacidad;
                System.out.println("La capacidad debe ser un número positivo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número entero.");
            }
        }
    }
    private String solicitarDiaSemana() {
        while (true) {
            System.out.println("Ingrese el día de la semana (LUNES, MARTES, etc. o 0 para cancelar):");
            String dia = scanner.nextLine().trim().toUpperCase();
            if (dia.equals("0")) return null;
            if (!dia.isEmpty()) return dia;
            System.out.println("Día no válido.");
        }
    }
    private BloqueHorario solicitarBloqueHorario() {
        System.out.println("Seleccione el bloque horario:");
        for (int i = 0; i < BloqueHorario.values().length; i++) {
            System.out.printf("%d. %s%n", i + 1, BloqueHorario.values()[i].getDescripcion());
        }
        System.out.println("0. Cancelar");
        int opcion = leerOpcion();
        if (opcion == 0) return null;
        if (opcion > 0 && opcion <= BloqueHorario.values().length) {
            return BloqueHorario.values()[opcion - 1];
        }
        System.out.println("Opción de bloque horario no válida.");
        return null;
    }

    private String solicitarEntrada(String mensaje) {
        System.out.print(mensaje);
        String entrada = scanner.nextLine().trim();
        if (entrada.equals("0")) {
            return null;
        }
        return entrada;
    }

    private <T> T seleccionarProfesorDeLista(List<T> lista, String accion) {
        profesorControlador.listarProfesores();
        return seleccionarElementoDeLista(lista, "profesor para " + accion);
    }

    private <T> T seleccionarAsignaturaDeLista(List<T> lista, String accion) {
        if (lista.isEmpty()) {
            System.out.println("No hay asignaturas disponibles.");
            return null;
        }
        System.out.println("\n--- Asignaturas Disponibles ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, lista.get(i).toString());
        }
        return seleccionarElementoDeLista(lista, "asignatura para " + accion);
    }

    private <T> T seleccionarSalaDeLista(List<T> lista, String accion) {
        salaControlador.listarSalas();
        return seleccionarElementoDeLista(lista, "sala para " + accion);
    }

    private <T> T seleccionarElementoDeLista(List<T> lista, String tipoElemento) {
        if (lista.isEmpty()) {
            mostrarNoHayElementosPara(tipoElemento);
            return null;
        }
        int seleccion = solicitarSeleccion("Seleccione el número del " + tipoElemento + " (0 para cancelar):", lista.size());
        if (seleccion == 0) { return null; }
        return lista.get(seleccion - 1);
    }

    private int solicitarSeleccion(String mensaje, int maxOpcion) {
        int seleccion;
        while (true) {
            System.out.print(mensaje + " ");
            try {
                seleccion = scanner.nextInt();
                scanner.nextLine();
                if (seleccion >= 0 && seleccion <= maxOpcion) {
                    return seleccion;
                } else {
                    System.out.println("Selección fuera de rango. Ingrese un número entre 0 y " + maxOpcion + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        System.out.println("\nÉXITO: " + mensaje);
    }

    private void mostrarMensajeError(String mensaje) {
        System.err.println("\nERROR: " + mensaje);
    }

    private void mostrarCancelacionOperacion(String operacion) {
        System.out.println("Operación de " + operacion + " cancelada.");
    }

    private void mostrarNoHayElementosPara(String tipo) {
        System.out.println("No hay " + tipo + " registrados.");
    }
}